package ahud.adaptivehud.screens.editscreen;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class Suggestor {
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)");
    private static final Style ERROR_STYLE;
    private static final Style INFO_STYLE;
    private static final List HIGHLIGHT_STYLES;
    final MinecraftClient client;
    private final Screen owner;
    final TextFieldWidget textField;
    final TextRenderer textRenderer;
    final int inWindowIndexOffset;
    final int maxSuggestionSize;
    final boolean chatScreenSized;
    final int color;
    private final List<OrderedText> messages = Lists.newArrayList();
    private int x;
    private int width;
    @Nullable
    private ParseResults<CommandSource> parse;
    @Nullable
    private CompletableFuture<Suggestions> pendingSuggestions;
    @Nullable
    private SuggestionWindow window;
    private boolean windowActive;
    boolean completingSuggestions;

    public Suggestor(MinecraftClient client, Screen owner, TextFieldWidget textField, TextRenderer textRenderer, int inWindowIndexOffset, int maxSuggestionSize, boolean chatScreenSized, int color) {
        this.client = client;
        this.owner = owner;
        this.textField = textField;
        this.textRenderer = textRenderer;
        this.inWindowIndexOffset = inWindowIndexOffset;
        this.maxSuggestionSize = maxSuggestionSize;
        this.chatScreenSized = chatScreenSized;
        this.color = color;
        textField.setRenderTextProvider(this::provideRenderText);
    }

    public void setWindowActive(boolean windowActive) {
        this.windowActive = windowActive;
        if (!windowActive) {
            this.window = null;
        }

    }

    public boolean keyPressed(int keyCode) {
        boolean bl = this.window != null;
        if (bl && this.window.keyPressed(keyCode)) {
            return true;
        } else if (this.owner.getFocused() != this.textField || keyCode != 258) {
            return false;
        } else {
            this.show();
            return true;
        }
    }

    public boolean mouseScrolled(double amount) {
        return this.window != null && this.window.mouseScrolled(MathHelper.clamp(amount, -1.0, 1.0));
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.window != null && this.window.mouseClicked((int)mouseX, (int)mouseY);
    }

    public void show() {
        if (this.pendingSuggestions != null && this.pendingSuggestions.isDone()) {
            Suggestions suggestions = this.pendingSuggestions.join();
            if (!suggestions.isEmpty()) {
                int i = 0;

                Suggestion suggestion;
                for(Iterator var4 = suggestions.getList().iterator(); var4.hasNext(); i = Math.max(i, this.textRenderer.getWidth(suggestion.getText()))) {
                    suggestion = (Suggestion)var4.next();
                }

                int j = MathHelper.clamp(this.textField.getCharacterX(suggestions.getRange().getStart()), 0, this.textField.getCharacterX(0) + this.textField.getInnerWidth() - i);
                int k = this.chatScreenSized ? this.owner.height - 12 : 72;
                this.window = new SuggestionWindow(j, k, i, this.sortSuggestions(suggestions));
            }
        }

    }

    public void clearWindow() {
        this.window = null;
    }

    private List<Suggestion> sortSuggestions(Suggestions suggestions) {
        String string = this.textField.getText().substring(0, this.textField.getCursor());
        int i = getStartOfCurrentWord(string);
        String string2 = string.substring(i).toLowerCase(Locale.ROOT);
        List<Suggestion> list = Lists.newArrayList();
        List<Suggestion> list2 = Lists.newArrayList();
        Iterator var7 = suggestions.getList().iterator();

        while(true) {
            while(var7.hasNext()) {
                Suggestion suggestion = (Suggestion)var7.next();
                if (!suggestion.getText().startsWith(string2) && !suggestion.getText().startsWith("minecraft:" + string2)) {
                    list2.add(suggestion);
                } else {
                    list.add(suggestion);
                }
            }

            list.addAll(list2);
            return list;
        }
    }

    public void refresh() {
        String string = this.textField.getText();
        if (this.parse != null && !this.parse.getReader().getString().equals(string)) {
            this.parse = null;
        }

        if (!this.completingSuggestions) {
            this.textField.setSuggestion(null);
            this.window = null;
        }

        this.messages.clear();
        StringReader stringReader = new StringReader(string);

        int i = this.textField.getCursor();
//        CommandDispatcher<CommandSource> commandDispatcher = this.client.player.networkHandler.getCommandDispatcher();
//        if (this.parse == null) {
//            this.parse = commandDispatcher.parse(stringReader, this.client.player.networkHandler.getCommandSource());
//        }
//
//        if (i >= 1 && (this.window == null || !this.completingSuggestions)) {
//            this.pendingSuggestions = commandDispatcher.getCompletionSuggestions(this.parse, i);
//            this.pendingSuggestions.thenRun(() -> {
//                if (this.pendingSuggestions.isDone()) {
//                    this.showCommandSuggestions();
//                }
//            });
//        }
    }

    private static int getStartOfCurrentWord(String input) {
        if (Strings.isNullOrEmpty(input)) {
            return 0;
        } else {
            int i = 0;

            for(Matcher matcher = WHITESPACE_PATTERN.matcher(input); matcher.find(); i = matcher.end()) {
            }

            return i;
        }
    }

    private static OrderedText formatException(CommandSyntaxException exception) {
        Text text = Texts.toText(exception.getRawMessage());
        String string = exception.getContext();
        return string == null ? text.asOrderedText() : Text.of("No variables could be found").asOrderedText();
    }

    private void showCommandSuggestions() {
        boolean bl = false;
        if (this.textField.getCursor() == this.textField.getText().length()) {
            if (this.pendingSuggestions.join().isEmpty() && !this.parse.getExceptions().isEmpty()) {
                int i = 0;
                Iterator var3 = this.parse.getExceptions().entrySet().iterator();

                while(var3.hasNext()) {
                    Map.Entry<CommandNode<CommandSource>, CommandSyntaxException> entry = (Map.Entry)var3.next();
                    CommandSyntaxException commandSyntaxException = entry.getValue();
                    if (commandSyntaxException.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
                        ++i;
                    } else {
                        this.messages.add(formatException(commandSyntaxException));
                    }
                }

                if (i > 0) {
                    this.messages.add(formatException(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create()));
                }
            } else if (this.parse.getReader().canRead()) {
                bl = true;
            }
        }

        this.x = 0;
        this.width = this.owner.width;
        if (this.messages.isEmpty() && !this.showUsages(Formatting.GRAY) && bl) {
            this.messages.add(formatException(CommandManager.getException(this.parse)));
        }

        this.window = null;
        if (this.windowActive && this.client.options.getAutoSuggestions().getValue()) {
            this.show();
        }

    }

    private boolean showUsages(Formatting formatting) {
        CommandContextBuilder<CommandSource> commandContextBuilder = this.parse.getContext();
        SuggestionContext<CommandSource> suggestionContext = commandContextBuilder.findSuggestionContext(this.textField.getCursor());
        Map<CommandNode<CommandSource>, String> map = this.client.player.networkHandler.getCommandDispatcher().getSmartUsage(suggestionContext.parent, this.client.player.networkHandler.getCommandSource());
        List<OrderedText> list = Lists.newArrayList();
        int i = 0;
        Style style = Style.EMPTY.withColor(formatting);
        Iterator var8 = map.entrySet().iterator();

        while(var8.hasNext()) {
            Map.Entry<CommandNode<CommandSource>, String> entry = (Map.Entry)var8.next();
            if (!(entry.getKey() instanceof LiteralCommandNode)) {
                list.add(OrderedText.styledForwardsVisitedString(entry.getValue(), style));
                i = Math.max(i, this.textRenderer.getWidth(entry.getValue()));
            }
        }

        if (!list.isEmpty()) {
            this.messages.addAll(list);
            this.x = MathHelper.clamp(this.textField.getCharacterX(suggestionContext.startPos), 0, this.textField.getCharacterX(0) + this.textField.getInnerWidth() - i);
            this.width = i;
            return true;
        } else {
            return false;
        }
    }

    private OrderedText provideRenderText(String original, int firstCharacterIndex) {
        return this.parse != null ? highlight(this.parse, original, firstCharacterIndex) : OrderedText.styledForwardsVisitedString(original, Style.EMPTY);
    }

    @Nullable
    static String getSuggestionSuffix(String original, String suggestion) {
        return suggestion.startsWith(original) ? suggestion.substring(original.length()) : null;
    }

    private static OrderedText highlight(ParseResults<CommandSource> parse, String original, int firstCharacterIndex) {
        List<OrderedText> list = Lists.newArrayList();
        int i = 0;
        int j = -1;
        CommandContextBuilder<CommandSource> commandContextBuilder = parse.getContext().getLastChild();
        Iterator var7 = commandContextBuilder.getArguments().values().iterator();

        while(var7.hasNext()) {
            ParsedArgument<CommandSource, ?> parsedArgument = (ParsedArgument)var7.next();
            ++j;
            if (j >= HIGHLIGHT_STYLES.size()) {
                j = 0;
            }

            int k = Math.max(parsedArgument.getRange().getStart() - firstCharacterIndex, 0);
            if (k >= original.length()) {
                break;
            }

            int l = Math.min(parsedArgument.getRange().getEnd() - firstCharacterIndex, original.length());
            if (l > 0) {
                list.add(OrderedText.styledForwardsVisitedString(original.substring(i, k), INFO_STYLE));
                list.add(OrderedText.styledForwardsVisitedString(original.substring(k, l), (Style)HIGHLIGHT_STYLES.get(j)));
                i = l;
            }
        }

        if (parse.getReader().canRead()) {
            int m = Math.max(parse.getReader().getCursor() - firstCharacterIndex, 0);
            if (m < original.length()) {
                int n = Math.min(m + parse.getReader().getRemainingLength(), original.length());
                list.add(OrderedText.styledForwardsVisitedString(original.substring(i, m), INFO_STYLE));
                list.add(OrderedText.styledForwardsVisitedString(original.substring(m, n), ERROR_STYLE));
                i = n;
            }
        }

        list.add(OrderedText.styledForwardsVisitedString(original.substring(i), INFO_STYLE));
        return OrderedText.concat(list);
    }

    public void render(DrawContext context, int mouseX, int mouseY) {
        if (!this.tryRenderWindow(context, mouseX, mouseY)) {
            this.renderMessages(context);
        }

    }

    public boolean tryRenderWindow(DrawContext context, int mouseX, int mouseY) {
        if (this.window != null) {
            this.window.render(context, mouseX, mouseY);
            return true;
        } else {
            return false;
        }
    }

    public void renderMessages(DrawContext context) {
        int i = 0;

        for(Iterator var3 = this.messages.iterator(); var3.hasNext(); ++i) {
            OrderedText orderedText = (OrderedText)var3.next();
            int j = this.chatScreenSized ? this.owner.height - 14 - 13 - 12 * i : 72 + 12 * i;
            context.fill(this.x - 1, j, this.x + this.width + 1, j + 12, this.color);
            context.drawTextWithShadow(this.textRenderer, orderedText, this.x, j + 2, -1);
        }

    }

    static {
        ERROR_STYLE = Style.EMPTY.withColor(Formatting.RED);
        INFO_STYLE = Style.EMPTY.withColor(Formatting.GRAY);

        HIGHLIGHT_STYLES = Stream.of(Formatting.AQUA, Formatting.YELLOW, Formatting.GREEN, Formatting.LIGHT_PURPLE, Formatting.GOLD)
        .map(Style.EMPTY::withColor)
        .collect(ImmutableList.toImmutableList());
    }

    @Environment(EnvType.CLIENT)
    public class SuggestionWindow {
        private final Rect2i area;
        private final String typedText;
        private final List<Suggestion> suggestions;
        private int inWindowIndex;
        private int selection;
        private Vec2f mouse;
        boolean completed;

        SuggestionWindow(final int x, final int y, final int width, final List<Suggestion> suggestions) {
            this.mouse = Vec2f.ZERO;
            int i = x - (Suggestor.this.textField.drawsBackground() ? 0 : 1);
            int j = Suggestor.this.chatScreenSized ? y - 3 - Math.min(suggestions.size(), Suggestor.this.maxSuggestionSize) * 12 : y - (Suggestor.this.textField.drawsBackground() ? 1 : 0);
            this.area = new Rect2i(i, j, width + 1, Math.min(suggestions.size(), Suggestor.this.maxSuggestionSize) * 12);
            this.typedText = Suggestor.this.textField.getText();
            this.suggestions = suggestions;
            this.select(0);
        }

        public void render(DrawContext context, int mouseX, int mouseY) {
            int i = Math.min(this.suggestions.size(), Suggestor.this.maxSuggestionSize);
            boolean bl = this.inWindowIndex > 0;
            boolean bl2 = this.suggestions.size() > this.inWindowIndex + i;
            boolean bl3 = bl || bl2;
            boolean bl4 = this.mouse.x != (float) mouseX || this.mouse.y != (float) mouseY;
            if (bl4) {
                this.mouse = new Vec2f((float) mouseX, (float) mouseY);
            }

            if (bl3) {
                context.fill(this.area.getX(), this.area.getY() - 1, this.area.getX() + this.area.getWidth(), this.area.getY(), Suggestor.this.color);
                context.fill(this.area.getX(), this.area.getY() + this.area.getHeight(), this.area.getX() + this.area.getWidth(), this.area.getY() + this.area.getHeight() + 1, Suggestor.this.color);
                int k;
                if (bl) {
                    for (k = 0; k < this.area.getWidth(); ++k) {
                        if (k % 2 == 0) {
                            context.fill(this.area.getX() + k, this.area.getY() - 1, this.area.getX() + k + 1, this.area.getY(), -1);
                        }
                    }
                }

                if (bl2) {
                    for (k = 0; k < this.area.getWidth(); ++k) {
                        if (k % 2 == 0) {
                            context.fill(this.area.getX() + k, this.area.getY() + this.area.getHeight(), this.area.getX() + k + 1, this.area.getY() + this.area.getHeight() + 1, -1);
                        }
                    }
                }
            }

            boolean bl5 = false;

            for (int l = 0; l < i; ++l) {
                Suggestion suggestion = this.suggestions.get(l + this.inWindowIndex);
                context.fill(this.area.getX(), this.area.getY() + 12 * l, this.area.getX() + this.area.getWidth(), this.area.getY() + 12 * l + 12, Suggestor.this.color);
                if (mouseX > this.area.getX() && mouseX < this.area.getX() + this.area.getWidth() && mouseY > this.area.getY() + 12 * l && mouseY < this.area.getY() + 12 * l + 12) {
                    if (bl4) {
                        this.select(l + this.inWindowIndex);
                    }

                    bl5 = true;
                }

                context.drawTextWithShadow(Suggestor.this.textRenderer, suggestion.getText(), this.area.getX() + 1, this.area.getY() + 2 + 12 * l, l + this.inWindowIndex == this.selection ? -256 : -5592406);
            }

            if (bl5) {
                Message message = this.suggestions.get(this.selection).getTooltip();
                if (message != null) {
                    context.drawTooltip(Suggestor.this.textRenderer, Texts.toText(message), mouseX, mouseY);
                }
            }

        }

        public boolean mouseClicked(int x, int y) {
            if (!this.area.contains(x, y)) {
                return false;
            } else {
                int i = (y - this.area.getY()) / 12 + this.inWindowIndex;
                if (i >= 0 && i < this.suggestions.size()) {
                    this.select(i);
                    this.complete();
                }

                return true;
            }
        }

        public boolean mouseScrolled(double amount) {
            int i = (int) (Suggestor.this.client.mouse.getX() * (double) Suggestor.this.client.getWindow().getScaledWidth() / (double) Suggestor.this.client.getWindow().getWidth());
            int j = (int) (Suggestor.this.client.mouse.getY() * (double) Suggestor.this.client.getWindow().getScaledHeight() / (double) Suggestor.this.client.getWindow().getHeight());
            if (this.area.contains(i, j)) {
                this.inWindowIndex = MathHelper.clamp((int) ((double) this.inWindowIndex - amount), 0, Math.max(this.suggestions.size() - Suggestor.this.maxSuggestionSize, 0));
                return true;
            } else {
                return false;
            }
        }

        public boolean keyPressed(int keyCode) {
            if (keyCode == 265) {
                this.scroll(-1);
                this.completed = false;
                return true;
            } else if (keyCode == 264) {
                this.scroll(1);
                this.completed = false;
                return true;
            } else if (keyCode == 258) {
                if (this.completed) {
                    this.scroll(Screen.hasShiftDown() ? -1 : 1);
                }

                this.complete();
                return true;
            } else if (keyCode == 256) {
                Suggestor.this.clearWindow();
                Suggestor.this.textField.setSuggestion(null);
                return true;
            } else {
                return false;
            }
        }

        public void scroll(int offset) {
            this.select(this.selection + offset);
            int i = this.inWindowIndex;
            int j = this.inWindowIndex + Suggestor.this.maxSuggestionSize - 1;
            if (this.selection < i) {
                this.inWindowIndex = MathHelper.clamp(this.selection, 0, Math.max(this.suggestions.size() - Suggestor.this.maxSuggestionSize, 0));
            } else if (this.selection > j) {
                this.inWindowIndex = MathHelper.clamp(this.selection + Suggestor.this.inWindowIndexOffset - Suggestor.this.maxSuggestionSize, 0, Math.max(this.suggestions.size() - Suggestor.this.maxSuggestionSize, 0));
            }

        }

        public void select(int index) {
            this.selection = index;
            if (this.selection < 0) {
                this.selection += this.suggestions.size();
            }

            if (this.selection >= this.suggestions.size()) {
                this.selection -= this.suggestions.size();
            }

            Suggestion suggestion = this.suggestions.get(this.selection);
            Suggestor.this.textField.setSuggestion(Suggestor.getSuggestionSuffix(Suggestor.this.textField.getText(), suggestion.apply(this.typedText)));

        }

        public void complete() {
            Suggestion suggestion = this.suggestions.get(this.selection);
            Suggestor.this.completingSuggestions = true;
            Suggestor.this.textField.setText(suggestion.apply(this.typedText));
            int i = suggestion.getRange().getStart() + suggestion.getText().length();
            Suggestor.this.textField.setSelectionStart(i);
            Suggestor.this.textField.setSelectionEnd(i);
            this.select(this.selection);
            Suggestor.this.completingSuggestions = false;
            this.completed = true;
        }
    }
}
