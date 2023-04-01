package com.demo.skyros.mapper;

import java.util.List;


public interface CommonMapper<E, VO> {

    List<VO> entityListToVOList(List<E> entityList);

    VO entityToVO(E e);

    E VOToEntity(VO vo);

    List<E> VOListToEntityList(List<VO> vos);
}
