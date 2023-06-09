package vn.edu.stu.luanvantotnghiep.service;

import java.util.List;
import java.util.Optional;

import vn.edu.stu.luanvantotnghiep.model.Banner;

public interface IBannerService {
    List<Banner> findAll();
    List<Banner> findAllByActive();
    Optional<Banner> findById(Integer id);
    Banner create(Banner banner);
    Banner update(Integer id, Banner banner);
    Banner delete(Integer id);
}
