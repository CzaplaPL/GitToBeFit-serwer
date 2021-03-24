package pl.umk.mat.git2befit.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class LongListConverter implements AttributeConverter<List<Long>, String> {
    private static final String SPLIT_CHAR = ";";

    @Override
    public String convertToDatabaseColumn(List<Long> longs) {
        return longs != null ? String.join(SPLIT_CHAR, String.valueOf(longs)) : "";
    }

    @Override
    public List<Long> convertToEntityAttribute(String s) {
        return s != null ? Arrays.stream(s.split(SPLIT_CHAR))
                .map(Long::parseLong)
                .collect(Collectors.toList()) : Collections.emptyList();
    }
}
