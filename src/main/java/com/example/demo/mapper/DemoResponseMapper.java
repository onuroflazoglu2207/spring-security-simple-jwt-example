package com.example.demo.mapper;

import com.example.demo.dto.DemoResponseDTO;
import com.example.demo.model.DemoModel;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface DemoResponseMapper {
    DemoResponseDTO toDemoResponseDTO(DemoModel demo);

    List<DemoResponseDTO> toDemoResponseDTOs(List<DemoModel> demos);

    DemoModel toDemoModel(DemoResponseDTO dto);

    List<DemoModel> toDemoModels(List<DemoResponseDTO> dtos);
}
