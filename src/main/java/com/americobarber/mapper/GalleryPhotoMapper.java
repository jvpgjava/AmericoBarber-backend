package com.americobarber.mapper;

import com.americobarber.dto.request.GalleryPhotoRequest;
import com.americobarber.dto.response.GalleryPhotoResponse;
import com.americobarber.entity.GalleryPhoto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GalleryPhotoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "displayOrder", defaultExpression = "java(0)")
    GalleryPhoto toEntity(GalleryPhotoRequest request);

    GalleryPhotoResponse toResponse(GalleryPhoto entity);
}
