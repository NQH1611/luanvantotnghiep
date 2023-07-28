package vn.edu.stu.luanvantotnghiep.service.Impl;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.edu.stu.luanvantotnghiep.model.NhaCungCap;
import vn.edu.stu.luanvantotnghiep.repository.NhaCungCapRepository;
import vn.edu.stu.luanvantotnghiep.service.INhaCungCapService;

@Service
public class NhaCungCapServiceImpl  implements INhaCungCapService{
    @Autowired
    private NhaCungCapRepository nhaCungCapRepository;

    @Override
    public List<NhaCungCap> findAll() {
        return nhaCungCapRepository.findAll();
    }

    @Override
    public Optional<NhaCungCap> findById(Integer id) {
       return nhaCungCapRepository.findById(id);
    }

    @Override
    public NhaCungCap create(NhaCungCap nhaCungCap) {
        nhaCungCap.setActive(1);
        nhaCungCap.setCreateDate(Calendar.getInstance().getTime());
        return nhaCungCapRepository.save(nhaCungCap);
    }

    @Override
    public NhaCungCap update(Integer id, NhaCungCap nhaCungCap) {
        Optional<NhaCungCap> data = nhaCungCapRepository.findById(id);
        if(data.isPresent()){
            data.get().setDiaChi(nhaCungCap.getDiaChi());
            data.get().setEmail(nhaCungCap.getEmail());
            data.get().setTenNhaCungCap(nhaCungCap.getTenNhaCungCap());
            data.get().setSoDienThoai(nhaCungCap.getSoDienThoai());
            data.get().setUpdateDate(Calendar.getInstance().getTime());
            return nhaCungCapRepository.save(data.get());
        }else{
            return null;
        }
        
    }

    @Override
    public NhaCungCap delete(Integer id) {
        Optional<NhaCungCap> result = nhaCungCapRepository.findById(id);
        if(result.isPresent()){
            NhaCungCap nhaCungCap = result.get();
            nhaCungCap.setActive(0);
            return nhaCungCapRepository.save(nhaCungCap);
        }
        return null;
    }

    @Override
    public Integer countNhaCungCap() {
        // TODO Auto-generated method stub
        return (int) nhaCungCapRepository.count();
    }
}
