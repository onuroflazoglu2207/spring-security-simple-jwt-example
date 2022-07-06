package com.example.demo.mapper;

import com.example.demo.dto.DemoRequestDTO;
import com.example.demo.model.DemoModel;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface DemoRequestMapper {

    DemoRequestDTO toDemoRequestDTO(DemoModel demo);

    List<DemoRequestDTO> toDemoRequestDTOs(List<DemoModel> demos);

    DemoModel toDemoModel(DemoRequestDTO dto);

    List<DemoModel> toDemoModels(List<DemoRequestDTO> dtos);
}
