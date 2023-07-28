package vn.edu.stu.luanvantotnghiep.service.Impl;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.edu.stu.luanvantotnghiep.model.KhuyenMai;
import vn.edu.stu.luanvantotnghiep.repository.KhuyenMaiRepository;
import vn.edu.stu.luanvantotnghiep.service.IKhuyenMaiService;

@Service
public class KhuyenMaiServiceImpl implements IKhuyenMaiService{
    @Autowired
    private KhuyenMaiRepository khuyenMaiRepository;

    @Override
    public List<KhuyenMai> findAll() {
        return khuyenMaiRepository.findAll();
    }

    @Override
    public Optional<KhuyenMai> findById(Integer id) {
        return khuyenMaiRepository.findById(id);
    }

    @Override
    public KhuyenMai create(KhuyenMai khuyenMai) {
        return khuyenMaiRepository.save(khuyenMai);
    }

    @Override
    public KhuyenMai update(Integer id, KhuyenMai khuyenMai) {
        Optional<KhuyenMai> data = khuyenMaiRepository.findById(id);
        if(data.isPresent()){
            data.get().setUpdateDate(Calendar.getInstance().getTime());
            data.get().setDescription(khuyenMai.getDescription());
            data.get().setCloseDate(khuyenMai.getCloseDate());
            data.get().setName(khuyenMai.getName());
            data.get().setPercentDiscount(khuyenMai.getPercentDiscount());
            return khuyenMaiRepository.save(data.get());
        }else{
            return null;
        }
    }

    @Override
    public KhuyenMai delete(Integer id) {
        Optional<KhuyenMai> result = khuyenMaiRepository.findById(id);
        if(result.isPresent()){
            KhuyenMai khuyenMai = result.get();
            khuyenMai.setActive(0);
            return khuyenMaiRepository.save(khuyenMai);
        }
        return null;
    }

    @Override
    public Integer countKhuyenMai() {
        // TODO Auto-generated method stub
        return (int) khuyenMaiRepository.count();
    }
}
