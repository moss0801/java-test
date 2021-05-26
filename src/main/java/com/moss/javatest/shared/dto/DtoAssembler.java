package com.moss.javatest.shared.dto;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Model을 Dto로 Dto를 Model로 변환하기 위한 Helper 함수 모음
 */
public class DtoAssembler {
    static final ModelMapper modelMapper;
    static {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMethodAccessLevel(Configuration.AccessLevel.PRIVATE);
    }

    /**
     * Source을 DestinationClass로 Convert
     * Modifier가 존재하면 modifier를 적용한 결과 반환
     */
    public static <S, D> D convert(S source, Class<D> destinationClass, BiFunction<S, D, D> modifier) {
        if (null == source) {
            return null;
        }
        // source to destination
        var destination = modelMapper.map(source, destinationClass);
        if (null == modifier) {
            return destination;
        }
        // modifier 적용
        destination = modifier.apply(source, destination);
        return destination;
    }

    /**
     * model을 dto로 변환
     */
    public static <D, M> D to(M model, Class<D> dtoClass, BiFunction<M, D, D> modifier) {
        return convert(model, dtoClass, modifier);
    }

    /**
     * model을 dto로 변환
     */
    public static <D, M> D to(M model, Class<D> dtoClass) {
        return to(model, dtoClass, null);
    }

    /**
     * models를 dtos로 변환
     */
    public static <D, M> List<D> to(List<M> models, Class<D> dtoClass) {
        return to(models, dtoClass, (BiFunction<M, D, D>)null);
    }

    /**
     * models를 dtos로 변환
     */
    public static <D, M> List<D> to(List<M> models, Class<D> dtoClass, BiFunction<M, D, D> modifier) {
        if (null == models) {
            return null;
        }
        return models.stream().map(model -> to(model, dtoClass, modifier)).collect(Collectors.toUnmodifiableList());
    }

    /**
     * dto로 부터 model을 생성
     */
    public static <D, M> M from(D dto, Class<M> modelClass, BiFunction<D, M, M> modifier) {
        return convert(dto, modelClass, modifier);
    }

    /**
     * dto로 부터 model을 생성
     */
    public static <D, M> M from(D dto, Class<M> modelClass) {
        return from(dto, modelClass, null);
    }

    /**
     * dtos로 부터 models를 생성
     */
    public static <D, M> List<M> from(List<D> dtos, Class<M> modelClass, BiFunction<D, M, M> modifier) {
        if (null == dtos) {
            return null;
        }
        return dtos.stream().map(dto -> from(dto, modelClass, modifier)).collect(Collectors.toList());
    }

    /**
     * dtos로 부터 models를 생성
     */
    public static <D, M> List<M> from(List<D> dtos, Class<M> modelClass) {
        return from(dtos, modelClass, (BiFunction<D, M, M>) null);
    }

    /**
     * Source를 Destination에 Mapping
     */
    public static <S, D> D map(S source, D destination) {
        return map(source, destination, null);
    }

    /**
     * Source를 Destination에 Mapping
     */
    public static <S, D> D map(S source, D destination, BiFunction<S, D, D> modifier) {
        modelMapper.map(source, destination);
        if (null != modifier) {
            destination = modifier.apply(source, destination);
        }
        return destination;
    }
}
