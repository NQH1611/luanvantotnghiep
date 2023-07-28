package vn.edu.stu.luanvantotnghiep.service.Impl;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.edu.stu.luanvantotnghiep.model.LoaiSanPham;
import vn.edu.stu.luanvantotnghiep.repository.LoaiSanPhamRepository;
import vn.edu.stu.luanvantotnghiep.service.ILoaiSanPhamService;

@Service
public class LoaiSanPhamServiceImpl implements ILoaiSanPhamService{
    @Autowired
    private LoaiSanPhamRepository loaiSanPhamRepository;

    @Override
    public List<LoaiSanPham> findAll() {
        return loaiSanPhamRepository.findByActive(1);
    }

    @Override
    public Optional<LoaiSanPham> findById(Integer id) {
        return loaiSanPhamRepository.findById(id);
    }

    @Override
    public LoaiSanPham create(LoaiSanPham loaiSanPham) {
        loaiSanPham.setActive(1);
        loaiSanPham.setCreateDate(Calendar.getInstance().getTime());
        return loaiSanPhamRepository.save(loaiSanPham);
    }

    @Override
    public LoaiSanPham update(Integer id,LoaiSanPham loaiSanPham) {
        Optional<LoaiSanPham> data = loaiSanPhamRepository.findById(id);
        if(data.isPresent()){
            data.get().setMoTa(loaiSanPham.getMoTa());
            data.get().setTenDanhMuc(loaiSanPham.getTenDanhMuc());;
            data.get().setUpdateDate(Calendar.getInstance().getTime());
            return loaiSanPhamRepository.save(data.get());
        }else{
            return null;
        }
    }

    @Override
    public LoaiSanPham delete(Integer id) {
        Optional<LoaiSanPham> result = loaiSanPhamRepository.findById(id);
        if(result.isPresent()){
            LoaiSanPham banner = result.get();
            banner.setActive(0);
            return loaiSanPhamRepository.save(banner);
        }
        return null;
    }

    @Override
    public Integer countLoaiSanPham() {
        // TODO Auto-generated method stub
        return (int) loaiSanPhamRepository.count();
    }
    @Override
    public LoaiSanPham findByName(String name){
        return loaiSanPhamRepository.findByTenDanhMucAndActive(name, 1);
    }
}
