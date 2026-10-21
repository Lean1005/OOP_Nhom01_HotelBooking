-- Kiểm tra và tạo cơ sở dữ liệu
IF NOT EXISTS (SELECT *
               FROM   sys.databases
               WHERE  name = 'HotelBookingDB')
    BEGIN
        CREATE DATABASE HotelBookingDB;
    END
GO
USE HotelBookingDB;
GO
-- Xóa bảng cũ theo thứ tự ngược lại để tránh vướng khóa ngoại
IF OBJECT_ID('TRG_SyncPhongStatus', 'TR') IS NOT NULL
    DROP TRIGGER TRG_SyncPhongStatus;

IF OBJECT_ID('Invoices', 'U') IS NOT NULL
    DROP TABLE Invoices;

IF OBJECT_ID('ServiceCharges', 'U') IS NOT NULL
    DROP TABLE ServiceCharges;

IF OBJECT_ID('PhieuDatPhong', 'U') IS NOT NULL
    DROP TABLE PhieuDatPhong;

IF OBJECT_ID('Phong', 'U') IS NOT NULL
    DROP TABLE Phong;

IF OBJECT_ID('KhachHang', 'U') IS NOT NULL
    DROP TABLE KhachHang;

IF OBJECT_ID('NguoiDung', 'U') IS NOT NULL
    DROP TABLE NguoiDung;
GO
-- 1. Bảng Người Dùng
CREATE TABLE NguoiDung (
    username  VARCHAR (50)   NOT NULL,
    password  VARCHAR (50)   NOT NULL,
    full_name NVARCHAR (100),
    phone     VARCHAR (10)  ,
    cccd      VARCHAR (12)  ,
    CONSTRAINT PK_NguoiDung PRIMARY KEY (username)
);

-- 2. Bảng Khách Hàng (Đổi sang NVARCHAR để không bị lỗi font Tiếng Việt)
CREATE TABLE KhachHang (
    cccd      VARCHAR (20)   NOT NULL,
    ho_ten    NVARCHAR (100) NOT NULL,
    sdt       VARCHAR (10)  ,
    gioi_tinh NVARCHAR (20) ,
    CONSTRAINT pk_khachhang PRIMARY KEY (cccd)
);

-- 3. Bảng Phong (Phải tạo trước PhieuDatPhong)
CREATE TABLE Phong (
    room_id            VARCHAR (10)    NOT NULL,
    floor              INT             NOT NULL,
    room_type_name     NVARCHAR (50)  ,
    price_per_night    DECIMAL (12, 2) NOT NULL,
    status             VARCHAR (30)    NOT NULL,
    cleaning_requested BIT             DEFAULT 0,
    max_occupacy       INT            ,
    CONSTRAINT pk_phong PRIMARY KEY (room_id)
);

-- 4. Bảng PhieuDatPhong
CREATE TABLE PhieuDatPhong (
    ma_dat_phong    INT           IDENTITY (1, 1) NOT NULL,
    cccd_dai_dien   VARCHAR (20)  NOT NULL,
    room_id         VARCHAR (10)  NOT NULL,
    ngay_nhan_phong DATE         ,
    ngay_tra_phong  DATE         ,
    trang_thai      NVARCHAR (50),
    tong_bill       INT          ,
    CONSTRAINT PK_PhieuDatPhong PRIMARY KEY (ma_dat_phong),
    CONSTRAINT FK_PhieuDatPhong_Phong FOREIGN KEY (room_id) REFERENCES Phong (room_id),
    CONSTRAINT FK_PhieuDatPhong_KhachHang FOREIGN KEY (cccd_dai_dien) REFERENCES KhachHang (cccd) ON DELETE NO ACTION ON UPDATE CASCADE
);

-- 5. Bảng ServiceCharges
CREATE TABLE ServiceCharges (
    charge_id    INT             IDENTITY (1, 1),
    booking_id   INT             NOT NULL,
    service_name NVARCHAR (100)  NOT NULL,
    amount       DECIMAL (18, 2) NOT NULL,
    service_type NVARCHAR (50)   NOT NULL,
    CONSTRAINT PK_ServiceCharges_Booking PRIMARY KEY (charge_id),
    CONSTRAINT FK_ServiceCharges_Bookings FOREIGN KEY (booking_id) REFERENCES PhieuDatPhong (ma_dat_phong)
);

-- 6. Bảng Invoices
CREATE TABLE Invoices (
    invoice_id            INT             IDENTITY (1, 1) PRIMARY KEY,
    booking_id            INT             NOT NULL UNIQUE,
    room_charge           DECIMAL (18, 2) NOT NULL,
    total_service_charges DECIMAL (18, 2) NOT NULL,
    grand_total           DECIMAL (18, 2) NOT NULL,
    issued_at             DATETIME        NOT NULL,
    CONSTRAINT FK_Invoices_Bookings FOREIGN KEY (booking_id) REFERENCES PhieuDatPhong (ma_dat_phong)
);


GO
-- TRIGGER TỰ ĐỘNG CẬP NHẬT TRẠNG THÁI PHÒNG KHI ĐẶT PHÒNG HOẶC CHECK-OUT
ALTER TRIGGER TRG_SyncPhongStatus
    ON PhieuDatPhong
    AFTER INSERT, UPDATE, DELETE
    AS BEGIN
           SET NOCOUNT ON;
           -- Khi thêm mới hoặc cập nhật đơn đang lưu trú -> Chuyển phòng sang OCCUPIED
           UPDATE p
           SET    p.status = 'OCCUPIED'
           FROM   Phong AS p
                  INNER JOIN
                  inserted AS i
                  ON p.room_id = i.room_id
           WHERE  i.trang_thai LIKE N'%Đang lưu trú%'
                  OR i.trang_thai LIKE N'%CHECKED_IN%';
           -- Khi Check-out -> Chuyển phòng về AVAILABLE và bật yêu cầu dọn phòng (cleaning_requested = 1)
           UPDATE p
           SET    p.status             = 'AVAILABLE',
                  p.cleaning_requested = 1
           FROM   Phong AS p
                  INNER JOIN
                  inserted AS i
                  ON p.room_id = i.room_id
           WHERE  (i.trang_thai LIKE N'%CHECKED_OUT%'
                   OR i.trang_thai LIKE N'%Đã trả phòng%'
                   OR i.trang_thai LIKE N'%Đã Check-out%'
                   OR i.trang_thai LIKE N'%CANCELLED%')
                  AND NOT EXISTS (SELECT 1
                                  FROM   PhieuDatPhong AS pdp
                                  WHERE  pdp.room_id = p.room_id
                                         AND pdp.ma_dat_phong <> i.ma_dat_phong
                                         AND pdp.trang_thai NOT LIKE N'%CHECKED_OUT%'
                                         AND pdp.trang_thai NOT LIKE N'%Đã trả phòng%'
                                         AND pdp.trang_thai NOT LIKE N'%Đã Check-out%'
                                         AND pdp.trang_thai NOT LIKE N'%CANCELLED%');
       END


GO
-- --- CHÈN DỮ LIỆU MẪU ---
INSERT  INTO NguoiDung (
    username,
    password,
    full_name,
    phone,
    cccd
)
VALUES                 ('admin', '123456', N'Quản trị viên Hệ thống', '0900000000', '001200000000'),
                       ('trangdepgai','2811',N'Hoàng Thị Huyền Trang','0904966183','044306009426');
SELECT * FROM NguoiDung
INSERT  INTO Phong (
    room_id,
    floor,
    room_type_name,
    price_per_night,
    status,
    cleaning_requested,
    max_occupacy
)
VALUES             -- Tầng 1 (Standard)
                   ('101', 1, N'Standard', 500000, 'AVAILABLE', 0, 2),
                   ('102', 1, N'Standard', 500000, 'OCCUPIED', 0, 2),
                   ('103', 1, N'Standard', 500000, 'AVAILABLE', 0, 2),
                   ('104', 1, N'Standard', 500000, 'AVAILABLE', 0, 2),
                   ('105', 1, N'Standard', 500000, 'AVAILABLE', 0, 2),
                   ('106', 1, N'Standard', 500000, 'AVAILABLE', 0, 2),
                   -- Tầng 2 (Standard)
                   ('201', 2, N'Standard', 500000, 'AVAILABLE', 0, 2),
                   ('202', 2, N'Standard', 500000, 'AVAILABLE', 0, 2),
                   ('203', 2, N'Standard', 500000, 'AVAILABLE', 0, 2),
                   ('204', 2, N'Standard', 500000, 'AVAILABLE', 0, 2),
                   ('205', 2, N'Standard', 500000, 'AVAILABLE', 0, 2),
                   ('206', 2, N'Standard', 500000, 'MAINTENANCE', 0, 2),
                   -- Tầng 3 (Deluxe)
                   ('301', 3, N'Deluxe', 900000, 'OCCUPIED', 0, 4),
                   ('302', 3, N'Deluxe', 900000, 'AVAILABLE', 0, 4),
                   ('303', 3, N'Deluxe', 900000, 'AVAILABLE', 0, 4),
                   ('304', 3, N'Deluxe', 900000, 'AVAILABLE', 0, 4),
                   ('305', 3, N'Deluxe', 900000, 'AVAILABLE', 0, 4),
                   ('306', 3, N'Deluxe', 900000, 'AVAILABLE', 0, 4),
                   -- Tầng 4 (Suite)
                   ('401', 4, N'Suite', 1800000, 'AVAILABLE', 0, 6),
                   ('402', 4, N'Suite', 1800000, 'OCCUPIED', 1, 6),
                   ('403', 4, N'Suite', 1800000, 'AVAILABLE', 0, 6),
                   ('404', 4, N'Suite', 1800000, 'AVAILABLE', 0, 6),
                   ('405', 4, N'Suite', 1800000, 'AVAILABLE', 0, 6),
                   ('406', 4, N'Suite', 1800000, 'AVAILABLE', 0, 6);
INSERT  INTO KhachHang (
    cccd,
    ho_ten,
    sdt,
    gioi_tinh
)
VALUES                 ('001204012345', N'Nguyễn Văn Minh', '0901234567', N'NAM'),
                       ('079203009876', N'Trần Thị Ngọc Lan', '0912345678', N'NU'),
                       ('036205005432', N'Lê Hoàng Nam', '0987654321', N'NAM');

-- Chèn PhieuDatPhong (Sẽ tự sinh ID tự tăng là 1, 2, 3)
INSERT  INTO PhieuDatPhong (
    cccd_dai_dien,
    room_id,
    ngay_nhan_phong,
    ngay_tra_phong,
    trang_thai,
    tong_bill
)
VALUES                     ('001204012345', '102', DATEADD(day, -1, GETDATE()), DATEADD(day, 2, GETDATE()), N'CONFIRMED', 1700000),
                           ('079203009876', '301', GETDATE(), DATEADD(day, 3, GETDATE()), N'CONFIRMED', 4100000),
                           ('036205005432', '402', DATEADD(day, -2, GETDATE()), DATEADD(day, 1, GETDATE()), N'CONFIRMED', 5800000);

-- Chèn Invoices khớp với booking_id 1, 2, 3
INSERT  INTO Invoices (
    booking_id,
    room_charge,
    total_service_charges,
    grand_total,
    issued_at
)
VALUES                (1, 1500000, 200000, 1700000, DATEADD(day, -13, GETDATE())),
                      (2, 2700000, 500000, 3200000, DATEADD(day, -12, GETDATE())),
                      (3, 5400000, 400000, 5800000, DATEADD(day, -11, GETDATE()));