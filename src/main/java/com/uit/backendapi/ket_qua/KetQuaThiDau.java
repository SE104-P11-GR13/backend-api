package com.uit.backendapi.ket_qua;

import com.uit.backendapi.ban_thang.BanThang;
import com.uit.backendapi.cau_thu.CauThu;
import com.uit.backendapi.lich.LichThiDau;
import com.uit.backendapi.thay_nguoi.ThayNguoi;
import com.uit.backendapi.the_phat.ThePhat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.util.LinkedHashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "KetQuaThiDau", schema = "dbo")
public class KetQuaThiDau {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaKetQua", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaLichThiDau", nullable = false)
    private LichThiDau maLichThiDau;

    @ColumnDefault("0")
    @Column(name = "SoBanDoiNha", nullable = false)
    private Integer soBanDoiNha;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CauThuXuatSac", nullable = false)
    private CauThu cauThuXuatSac;

    @Nationalized
    @Column(name = "GhiChu", length = 500)
    private String ghiChu;

    @ColumnDefault("0")
    @Column(name = "SoBanDoiKhach", nullable = false)
    private Integer soBanDoiKhach;

    @OneToMany(mappedBy = "maKetQua")
    private Set<BanThang> banThangs = new LinkedHashSet<>();

    @OneToMany(mappedBy = "maKetQua")
    private Set<ThayNguoi> thayNguois = new LinkedHashSet<>();

    @OneToMany(mappedBy = "maKetQua")
    private Set<ThePhat> thePhats = new LinkedHashSet<>();

}