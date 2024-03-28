package com.lzh.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzh.web.model.entity.Generator;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 数据库操作类
 * @author lzh
 */
public interface GeneratorMapper extends BaseMapper<Generator> {
    @Select("SELECT id, distPath FROM generator WHERE isDelete = 1")
    List<Generator> listDeletedGenerator();
}




