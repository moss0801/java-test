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
     * Input을 ResultClass로 Convert
     * Modifier가 존재하면 modifier를 적용한 결과 반환
     */
    public static <I, R> R convert(I input, Class<R> resultClass, BiFunction<R, I, R> modifier) {
        if (null == input) {
            return null;
        }
        // input to result
        var result = modelMapper.map(input, resultClass);
        if (null == modifier) {
            return result;
        }
        // modifier 적용
        modifier.apply(result, input);
        return result;
    }

    /**
     * model을 dto로 변환
     */
    public static <D, M> D to(M model, Class<D> dtoClass, BiFunction<D, M, D> modifier) {
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
        return to(models, dtoClass, (BiFunction<D, M, D>)null);
    }

    /**
     * models를 dtos로 변환
     */
    public static <D, M> List<D> to(List<M> models, Class<D> dtoClass, BiFunction<D, M, D> modifier) {
        if (null == models) {
            return null;
        }
        return models.stream().map(model -> to(model, dtoClass, modifier)).collect(Collectors.toUnmodifiableList());
    }

    /**
     * dto로 부터 model을 생성
     */
    public static <D, M> M from(D dto, Class<M> modelClass, BiFunction<M, D, M> modifier) {
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
    public static <D, M> List<M> from(List<D> dtos, Class<M> modelClass, BiFunction<M, D, M> modifier) {
        if (null == dtos) {
            return null;
        }
        return dtos.stream().map(dto -> from(dto, modelClass, modifier)).collect(Collectors.toList());
    }

    /**
     * dtos로 부터 models를 생성
     */
    public static <D, M> List<M> from(List<D> dtos, Class<M> modelClass) {
        return from(dtos, modelClass, (BiFunction<M, D, M>) null);
    }
}
