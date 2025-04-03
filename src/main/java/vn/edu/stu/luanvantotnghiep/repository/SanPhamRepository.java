package vn.edu.stu.luanvantotnghiep.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.edu.stu.luanvantotnghiep.model.LoaiSanPham;
import vn.edu.stu.luanvantotnghiep.model.NhaSanXuat;
import vn.edu.stu.luanvantotnghiep.model.SanPham;

public interface SanPhamRepository extends JpaRepository<SanPham, Integer>{
    @Query(value = "select * from san_pham where trang_thai = 1 and danh_muc = :danhmuc", nativeQuery = true)
    List<SanPham> findSanPhamByDanhMucActive(@Param("danhmuc") Integer danhMuc);
    @Query(value = "select * from san_pham where trang_thai = 1 and nha_san_xuat = :nhasanxuat", nativeQuery = true)
    List<SanPham> findSanPhamByNhaSanXuatActive(@Param("nhasanxuat") Integer nhaSanXuat);
    @Query(value = "select * from san_pham where trang_thai = 1", nativeQuery = true)
    Page<SanPham> findSanPhamActive(PageRequest pageRequest);
    Integer countByTrangThai(Integer active);
    Integer countByTrangThaiAndCreateDate(Integer active, Date createDate);
    List<SanPham> findByNhaSanXuat(NhaSanXuat nhaSanXuat);
    List<SanPham> findByDanhMucAndTrangThai(LoaiSanPham loaiSanPham, Integer trangThai);
    // @Query(value = "SELECT DISTINCT * FROM san_pham a JOIN thuoc_tinh b on a.id = b.san_pham_id JOIN nha_san_xuat c ON a.nha_san_xuat = c.id JOIN danh_muc d ON a.danh_muc = d.id WHERE a.ten_san_pham LIKE %?1% OR b.gia_tri_thuoc_tinh LIKE %?1% OR c.ten_nha_san_xuat LIKE %?1% OR d.ten_danh_muc LIKE %?1% AND a.trang_thai = 1" , nativeQuery = true)
    // List<SanPham> search(String keyword);
    List<SanPham> findByNhaSanXuatAndDanhMucAndTrangThaiAndGiaBetween(NhaSanXuat nhasanxuat, LoaiSanPham loaisanpham,Integer trangThai, Double tugia, Double dengia);
    @Query(value="SELECT * FROM san_pham where trang_thai = 1 order by gia desc limit 10", nativeQuery = true)
    List<SanPham>  findSanPhamByGiaCaoNhat();
    public List<SanPham>findByTrangThai(Integer active);
    public List<SanPham> findBySoLuongTonAndTrangThai(Integer soLuongTon, Integer trangThai);
    public Integer countByNhaSanXuat(NhaSanXuat nhaSanXuat);
    public Integer countByDanhMuc(LoaiSanPham loaiSanPham);
}
