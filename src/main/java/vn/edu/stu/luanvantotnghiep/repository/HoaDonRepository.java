package vn.edu.stu.luanvantotnghiep.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.edu.stu.luanvantotnghiep.model.HoaDon;

public interface HoaDonRepository extends JpaRepository<HoaDon, Integer>{
    @Query(value = "select * from hoa_don where trang_thai = 1", nativeQuery = true)
    List<HoaDon> findAllHoaDonActive();
    @Query(value = "select * from hoa_don where khach_hang_id = :khachhang", nativeQuery = true)
    List<HoaDon> findAllHoaDonByKhachHang(@Param("khachhang") Integer khachHang);
    Integer countByCreateDate(Date createDate);
    
    List<HoaDon> findAllByOrderByCreateDateDesc(PageRequest pageRequest);
}
