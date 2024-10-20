package com.uit.backendapi.bxh;

import com.uit.backendapi.bxh.dto.CreateBxhDto;
import com.uit.backendapi.bxh.dto.UpdateBxhDto;
import com.uit.backendapi.doi_bong.DoiBong;
import com.uit.backendapi.lich.LichThiDau;
import com.uit.backendapi.mua_giai.MuaGiai;

import java.util.List;

public interface IBangXepHangService {
    List<BangXepHang> getBangXepHangByMuaGiai(String namOrId);
    List<BangXepHang> getBangXepHangByMuaGiai(MuaGiai muaGiai);
    BangXepHang getBangXepHangByMaDoiAndMuaGiai(Long maDoi, Long muaGiai);
    BangXepHang getBangXepHangByMaDoiAndMuaGiai(DoiBong doiBong, MuaGiai muaGiai);
    BangXepHang createBangXepHang(CreateBxhDto createBxhDto);
    void createBangXepHang(DoiBong doiBong, MuaGiai muaGiai);
    BangXepHang updateBangXepHang(Long id, UpdateBxhDto updateBxhDto);
    void updateBangXepHang(DoiBong doiNha, DoiBong doiKhach, MuaGiai muaGiai, int soBanThangDoiNha, int soBanThangDoiKhach);
    void deleteBangXepHang(Long id);
}
