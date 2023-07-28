package vn.edu.stu.luanvantotnghiep.service.Impl;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.edu.stu.luanvantotnghiep.model.NhaSanXuat;
import vn.edu.stu.luanvantotnghiep.repository.NhaSanXuatRepository;
import vn.edu.stu.luanvantotnghiep.service.INhaSanXuatService;

@Service
public class NhaSanXuatServiceImpl implements INhaSanXuatService{
    @Autowired
    private NhaSanXuatRepository nhaSanXuatRepository;

    @Override
    public List<NhaSanXuat> findAll() {
        return nhaSanXuatRepository.findAll();
    }

    @Override
    public Optional<NhaSanXuat> findById(Integer id) {
        return nhaSanXuatRepository.findById(id);
    }

    @Override
    public NhaSanXuat create(NhaSanXuat nhaSanXuat) {
        nhaSanXuat.setCreateDate(Calendar.getInstance().getTime());
        nhaSanXuat.setActive(1);
        return nhaSanXuatRepository.save(nhaSanXuat);
    }

    @Override
    public NhaSanXuat update(Integer id, NhaSanXuat nhaSanXuat) {
        Optional<NhaSanXuat> data = nhaSanXuatRepository.findById(id);
        if(data.isPresent()){
            data.get().setMoTa(nhaSanXuat.getMoTa());
            data.get().setTenNhaSanXuat(nhaSanXuat.getTenNhaSanXuat());;
            data.get().setUpdateDate(Calendar.getInstance().getTime());
            return nhaSanXuatRepository.save(data.get());
        }else{
            return null;
        }
    }

    @Override
    public NhaSanXuat delete(Integer id) {
        Optional<NhaSanXuat> result = nhaSanXuatRepository.findById(id);
        if(result.isPresent()){
            NhaSanXuat nhaCungCap = result.get();
            nhaCungCap.setActive(0);
            return nhaSanXuatRepository.save(nhaCungCap);
        }
        return null;
    }

    @Override
    public Integer countNhaSanXuat() {
        // TODO Auto-generated method stub
        return (int) nhaSanXuatRepository.count();
    }

    @Override
    public NhaSanXuat findByTenNhaSanXuat(String name) {
        return nhaSanXuatRepository.findByTenNhaSanXuat(name);
    }

}
