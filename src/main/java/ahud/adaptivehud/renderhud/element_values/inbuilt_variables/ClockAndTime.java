package ahud.adaptivehud.renderhud.element_values.inbuilt_variables;

import ahud.adaptivehud.renderhud.element_values.annotations.LocalFlagName;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClockAndTime {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final PlayerEntity player = client.player;

    public String days() {
        return String.valueOf(client.world.getTimeOfDay() / 24000L);
    }

    public String time_irl(@LocalFlagName("format") List<String> format, @LocalFlagName("timezone") List<String> timezone) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format.getFirst());
        LocalDateTime timeZone;
        if (timezone != null) {
            ZoneId zoneId = ZoneId.of(timezone.getFirst());
            timeZone = LocalDateTime.now(zoneId);
        } else {
            timeZone = LocalDateTime.now();
        }
        return timeZone.format(formatter);
    }

    public String time_ing(@LocalFlagName("format") List<String> format) {
        long time = MinecraftClient.getInstance().world.getTimeOfDay() % 24000;

        int hour24 = (int)((time / 1000 + 6) % 24); // 0-23 hour in 24-hour format
        int hour12 = hour24 % 12 == 0 ? 12 : hour24 % 12; // 1-12 hour in 12-hour format
        int minutes = (int)((time % 1000) * 60 / 1000);
        int seconds = (int)((time % 1000) * 60 % 1000 / 16.6667); // Approx seconds from ticks

        return format.getFirst()
                .replace("HH", String.format("%02d", hour24)) // Two-digit 24-hour
                .replace("H", String.valueOf(hour24))        // Single-digit 24-hour
                .replace("hh", String.format("%02d", hour12)) // Two-digit 12-hour
                .replace("h", String.valueOf(hour12))        // Single-digit 12-hour
                .replace("mm", String.format("%02d", minutes)) // Two-digit minutes
                .replace("m", String.valueOf(minutes))       // Single-digit minutes
                .replace("ss", String.format("%02d", seconds)) // Two-digit seconds
                .replace("s", String.valueOf(seconds));      // Single-digit seconds
    }

    public String is_am() {
        return String.valueOf(((int)(((client.world.getTimeOfDay() % 24000) / 1000 + 6) % 24)) < 12);
    }

    public String is_pm() {
        return String.valueOf(!(((int)(((client.world.getTimeOfDay() % 24000) / 1000 + 6) % 24)) < 12));
    }

    public String ampm() {
        return ((int)(((client.world.getTimeOfDay() % 24000) / 1000 + 6) % 24)) < 12 ? "AM" : "PM";
    }

    public String time() {
        return String.valueOf(client.world.getTimeOfDay() % 24000L);
    }

    public String lunar_time() {
        return String.valueOf(client.world.getLunarTime());
    }
}
