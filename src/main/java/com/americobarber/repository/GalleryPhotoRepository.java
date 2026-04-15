package com.americobarber.repository;

import com.americobarber.entity.GalleryPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GalleryPhotoRepository extends JpaRepository<GalleryPhoto, Long> {

    List<GalleryPhoto> findAllByOrderByDisplayOrderAsc();
}
