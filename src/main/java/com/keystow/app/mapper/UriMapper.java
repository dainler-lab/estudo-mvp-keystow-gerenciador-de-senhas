package com.keystow.app.mapper;

import com.keystow.app.controller.form.UriFormDto;
import com.keystow.app.model.Uri;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UriMapper {

	public UriFormDto toFormDto(Uri uri) {
		UriFormDto uriFormDto = new UriFormDto();
		uriFormDto.setId(uri.getId());
		uriFormDto.setValor(uri.getValor());
		return uriFormDto;
	}

	public Uri toModel(UriFormDto uriFormDto) {
		Uri uri = new Uri();
		uri.setId(uriFormDto.getId());
		uri.setValor(uriFormDto.getValor());
		return uri;
	}

	// @formatter:off
    public List<Uri> toModelList(List<UriFormDto> uriFormDtoList) {
        return Optional.ofNullable(uriFormDtoList)
				.orElseGet(List::of).stream()
				.map(this::toModel)
				.collect(Collectors.toList());
    }

    public List<UriFormDto> toDtoList(List<Uri> urisModelList) {
        return Optional.ofNullable(urisModelList)
					   .orElseGet(List::of).stream()
					   .map(this::toFormDto)
					   .collect(Collectors.toList());
    }

}
