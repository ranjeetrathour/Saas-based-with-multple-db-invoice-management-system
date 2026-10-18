package com.moptra.invoice.comman.mappers;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenericMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    static {
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
    }

    /**
     * Generic mapper
     *
     * @param sourceObject
     * @param targetClass
     * @param <T_SOURCE>
     * @param <T_TARGET>
     * @return
     */
    public static <T_SOURCE, T_TARGET> T_TARGET map(T_SOURCE sourceObject, Class<T_TARGET> targetClass) {
        return sourceObject == null ? null : modelMapper.map(sourceObject, targetClass);
    }

}
