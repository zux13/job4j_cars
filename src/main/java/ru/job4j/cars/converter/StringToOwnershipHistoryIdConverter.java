package ru.job4j.cars.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.job4j.cars.model.OwnershipHistoryId;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class StringToOwnershipHistoryIdConverter implements Converter<String, OwnershipHistoryId> {

    private static final Pattern PATTERN = Pattern.compile("carId=(\\d+),\\s*ownerId=(\\d+),\\s*historyId=(\\d+)");

    @Override
    public OwnershipHistoryId convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }

        try {
            Matcher matcher = PATTERN.matcher(source);
            if (!matcher.find()) {
                throw new IllegalArgumentException("Invalid format: " + source);
            }

            int carId = Integer.parseInt(matcher.group(1));
            int ownerId = Integer.parseInt(matcher.group(2));
            int historyId = Integer.parseInt(matcher.group(3));

            return new OwnershipHistoryId(carId, ownerId, historyId);

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert: " + source, e);
        }
    }

}
