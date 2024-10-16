package com.keystow.app.mapper;

import com.keystow.app.controller.list.ItemCollectionPesquisaDto;
import com.keystow.app.model.ItemDoCofre;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@AllArgsConstructor
@Component
public class ItemCollectionPesquisaDtoMapper {

    private final ModelMapper modelMapper;

    public ItemCollectionPesquisaDto toFormDto(ItemDoCofre itemDoCofre) {
        return modelMapper.map(itemDoCofre, ItemCollectionPesquisaDto.class);
    }

    public ItemDoCofre toModel(ItemCollectionPesquisaDto itemCollectionPesquisaDto) {
        return modelMapper.map(itemCollectionPesquisaDto, ItemDoCofre.class);
    }

    public List<ItemCollectionPesquisaDto> toFormDtoList(List<ItemDoCofre> itemDoCofreList) {
        return itemDoCofreList.stream().map(this::toFormDto).toList();
    }

    public List<ItemDoCofre> toModelList(List<ItemCollectionPesquisaDto> itemCollectionPesquisaDtoList) {
        return itemCollectionPesquisaDtoList.stream().map(this::toModel).toList();
    }

}
