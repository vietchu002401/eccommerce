-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th8 01, 2023 lúc 11:57 AM
-- Phiên bản máy phục vụ: 10.4.28-MariaDB
-- Phiên bản PHP: 8.0.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `ecommerce`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `cart`
--

CREATE TABLE `cart` (
  `id` bigint(20) NOT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `status` bit(1) NOT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `cart`
--

INSERT INTO `cart` (`id`, `created_date`, `status`, `updated_date`, `user_id`) VALUES
(2, '2023-07-25 10:31:54.000000', b'1', '2023-07-25 10:31:54.000000', 1),
(3, '2023-07-27 13:42:51.000000', b'1', '2023-07-27 13:42:51.000000', 2);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `cart_item`
--

CREATE TABLE `cart_item` (
  `id` bigint(20) NOT NULL,
  `cart_id` bigint(20) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  `quantity` int(11) NOT NULL,
  `status` bit(1) NOT NULL,
  `sub_total` double DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `cart_item`
--

INSERT INTO `cart_item` (`id`, `cart_id`, `created_date`, `product_id`, `quantity`, `status`, `sub_total`, `updated_date`) VALUES
(23, 2, '2023-07-31 15:01:06.000000', 7, 1, b'1', 80000, '2023-07-31 15:01:06.000000');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `category`
--

CREATE TABLE `category` (
  `id` bigint(20) NOT NULL,
  `category_image` varchar(255) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `status` bit(1) NOT NULL,
  `updated_date` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `category`
--

INSERT INTO `category` (`id`, `category_image`, `created_date`, `description`, `name`, `status`, `updated_date`) VALUES
(4, '/uploads/figure.jpg', '2023-07-25 10:07:28.000000', 'Auth Figure', 'Figure', b'1', '2023-07-26 11:03:45.000000'),
(5, '/uploads/manga.jpg', '2023-07-25 10:08:24.000000', 'Auth Manga', 'Manga', b'1', '2023-07-25 10:08:24.000000'),
(6, '/uploads/keycap.jpg', '2023-07-25 10:09:46.000000', 'Auth Keycap', 'Keycap', b'1', '2023-07-25 10:09:46.000000'),
(13, '/uploads/genshin.jpg', '2023-07-28 17:09:25.000000', 'Poster anime', 'Poster', b'1', '2023-07-28 17:09:25.000000');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `coupon`
--

CREATE TABLE `coupon` (
  `id` bigint(20) NOT NULL,
  `code` varchar(255) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `discount_percent` int(11) DEFAULT NULL,
  `expiration_date` varchar(255) DEFAULT NULL,
  `max_usage` int(11) DEFAULT NULL,
  `status` bit(1) NOT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  `coupon_type` enum('FIXED','PERCENT') DEFAULT NULL,
  `min_total_price` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `coupon`
--

INSERT INTO `coupon` (`id`, `code`, `created_date`, `discount_percent`, `expiration_date`, `max_usage`, `status`, `updated_date`, `coupon_type`, `min_total_price`) VALUES
(12, 'FSTRGdfTQWffz', '2023-07-31 11:23:09.000000', 20, '2023-08-15T00:00:00.000+07:00', 20, b'1', '2023-07-31 11:23:09.000000', 'PERCENT', 100000),
(13, 'FSTRGDF', '2023-07-31 11:25:31.000000', 20000, '2023-08-15T00:00:00.000+07:00', 19, b'1', '2023-07-31 11:25:31.000000', 'FIXED', 100000),
(14, 'FSTRGDFFG', '2023-07-31 11:35:39.000000', 8, '2023-08-15T00:00:00.000+07:00', 20, b'1', '2023-07-31 11:35:39.000000', 'PERCENT', 70000);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `orders`
--

CREATE TABLE `orders` (
  `id` bigint(20) NOT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `status_shipping` bit(1) NOT NULL,
  `total_price` double DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `user_payment_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `orders`
--

INSERT INTO `orders` (`id`, `created_date`, `status_shipping`, `total_price`, `updated_date`, `user_id`, `user_payment_id`) VALUES
(3, '2023-07-25 10:36:48.000000', b'1', 25000, '2023-07-25 10:36:48.000000', 1, 1),
(4, '2023-07-25 10:59:22.000000', b'1', 50000, '2023-07-25 10:59:22.000000', 1, 1),
(5, '2023-07-25 11:00:56.000000', b'1', 50000, '2023-07-25 11:00:56.000000', 1, 1),
(6, '2023-07-26 09:45:46.000000', b'1', 300000, '2023-07-26 09:45:46.000000', 1, 1),
(7, '2023-07-26 09:57:01.000000', b'1', 250000, '2023-07-26 09:57:01.000000', 1, 1),
(8, '2023-07-26 10:01:39.000000', b'1', 250000, '2023-07-26 10:01:39.000000', 1, 1),
(9, '2023-07-26 10:05:33.000000', b'1', 250000, '2023-07-26 10:05:33.000000', 1, 1),
(10, '2023-07-27 13:40:24.000000', b'1', 247500, '2023-07-27 13:40:24.000000', 1, 1),
(11, '2023-07-27 13:44:15.000000', b'1', 25000, '2023-07-27 13:44:15.000000', 2, 2),
(12, '2023-07-31 14:49:05.000000', b'1', 140000, '2023-07-31 14:49:05.000000', 1, 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `order_item`
--

CREATE TABLE `order_item` (
  `id` bigint(20) NOT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `order_id` bigint(20) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `sub_total` double DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `order_item`
--

INSERT INTO `order_item` (`id`, `created_date`, `order_id`, `product_id`, `quantity`, `sub_total`, `updated_date`) VALUES
(6, '2023-07-25 10:36:49.000000', 3, 8, 1, 25000, '2023-07-25 10:36:49.000000'),
(7, '2023-07-25 10:59:22.000000', 4, 8, 1, 25000, '2023-07-25 10:59:22.000000'),
(8, '2023-07-25 10:59:22.000000', 4, 9, 1, 25000, '2023-07-25 10:59:22.000000'),
(9, '2023-07-25 11:00:56.000000', 5, 9, 1, 25000, '2023-07-25 11:00:56.000000'),
(10, '2023-07-25 11:00:56.000000', 5, 8, 1, 25000, '2023-07-25 11:00:56.000000'),
(11, '2023-07-26 09:45:46.000000', 6, 8, 1, 25000, '2023-07-26 09:45:46.000000'),
(12, '2023-07-26 09:45:46.000000', 6, 9, 1, 25000, '2023-07-26 09:45:46.000000'),
(13, '2023-07-26 09:45:46.000000', 6, 10, 1, 250000, '2023-07-26 09:45:46.000000'),
(14, '2023-07-26 09:57:01.000000', 7, 10, 1, 250000, '2023-07-26 09:57:01.000000'),
(15, '2023-07-26 10:01:39.000000', 8, 10, 1, 250000, '2023-07-26 10:01:39.000000'),
(16, '2023-07-26 10:05:33.000000', 9, 11, 1, 250000, '2023-07-26 10:05:33.000000'),
(17, '2023-07-27 13:40:24.000000', 10, 11, 1, 250000, '2023-07-27 13:40:24.000000'),
(18, '2023-07-27 13:40:24.000000', 10, 9, 1, 25000, '2023-07-27 13:40:24.000000'),
(19, '2023-07-27 13:44:15.000000', 11, 9, 1, 25000, '2023-07-27 13:44:15.000000'),
(20, '2023-07-31 14:49:05.000000', 12, 7, 2, 160000, '2023-07-31 14:49:05.000000');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `product`
--

CREATE TABLE `product` (
  `id` bigint(20) NOT NULL,
  `amount` int(11) NOT NULL,
  `category_id` bigint(20) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `status` bit(1) NOT NULL,
  `updated_date` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `product`
--

INSERT INTO `product` (`id`, `amount`, `category_id`, `created_date`, `description`, `name`, `price`, `status`, `updated_date`) VALUES
(6, 100, 4, '2023-07-25 10:13:12.000000', 'Desceiption', 'Figure Nahida', 100000, b'1', '2023-07-25 10:13:12.000000'),
(7, 98, 4, '2023-07-25 10:15:47.000000', 'Desceiption', 'Figure Kazuha', 80000, b'1', '2023-07-31 14:49:05.000000'),
(8, 98, 5, '2023-07-25 10:17:17.000000', 'Manga', 'Kimetsu no yaiba epsoide 1', 25000, b'1', '2023-07-26 09:45:46.000000'),
(9, 97, 5, '2023-07-25 10:17:37.000000', 'Manga', 'Kimetsu no yaiba epsoide 2', 25000, b'1', '2023-07-27 13:44:15.000000'),
(10, 97, 6, '2023-07-25 10:18:28.000000', 'Keycap', 'Keycap Nahida', 250000, b'1', '2023-07-26 10:01:39.000000'),
(11, 98, 6, '2023-07-25 10:19:44.000000', 'Keycap', 'Keycap Raiden Shogun', 250000, b'1', '2023-07-27 13:40:24.000000'),
(12, 100, 13, '2023-07-28 17:24:47.000000', 'Poster', 'Poster Raiden Shogun', 10000, b'1', '2023-07-28 17:24:47.000000'),
(13, 100, 4, '2023-08-01 13:42:25.000000', 'Figure Ganyu', 'Figure Ganyu', 500000, b'1', '2023-08-01 13:42:25.000000'),
(14, 100, 4, '2023-08-01 13:43:55.000000', 'Figure Raiden Shogun', 'Figure Raiden Shogun', 900000, b'1', '2023-08-01 13:43:55.000000'),
(15, 100, 4, '2023-08-01 13:45:49.000000', 'Figure Nezuko', 'Figure Nezuko', 1100000, b'1', '2023-08-01 13:45:49.000000'),
(16, 100, 4, '2023-08-01 13:47:02.000000', 'Figure Kamisato Ayaka', 'Figure Kamisato Ayaka', 1100000, b'1', '2023-08-01 13:47:02.000000'),
(17, 100, 4, '2023-08-01 13:48:17.000000', 'Figure HuTao', 'Figure HuTao', 700000, b'1', '2023-08-01 13:48:17.000000'),
(18, 100, 4, '2023-08-01 13:50:05.000000', 'Figure Keqing', 'Figure Keqing', 800000, b'1', '2023-08-01 13:50:05.000000'),
(19, 100, 4, '2023-08-01 13:51:19.000000', 'Figure Nilou', 'Figure Nilou', 1500000, b'1', '2023-08-01 13:51:19.000000');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `product_image`
--

CREATE TABLE `product_image` (
  `id` bigint(20) NOT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  `source_image` varchar(255) DEFAULT NULL,
  `status` bit(1) NOT NULL,
  `updated_date` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `product_image`
--

INSERT INTO `product_image` (`id`, `created_date`, `product_id`, `source_image`, `status`, `updated_date`) VALUES
(4, '2023-07-25 10:13:12.000000', 6, '/uploads/nahida-figure.jpg', b'1', '2023-07-25 10:13:12.000000'),
(5, '2023-07-25 10:13:12.000000', 6, '/uploads/figure-nahida-2.jpg', b'1', '2023-07-25 10:13:12.000000'),
(6, '2023-07-25 10:15:47.000000', 7, '/uploads/figure-kazuha.png', b'1', '2023-07-25 10:15:47.000000'),
(7, '2023-07-25 10:15:47.000000', 7, '/uploads/8.png', b'1', '2023-07-25 10:15:47.000000'),
(8, '2023-07-25 10:17:17.000000', 8, '/uploads/kimetsu-ep1.jpg', b'1', '2023-07-25 10:17:17.000000'),
(9, '2023-07-25 10:17:37.000000', 9, '/uploads/kimetsu-ep2.jpg', b'1', '2023-07-25 10:17:37.000000'),
(10, '2023-07-25 10:18:28.000000', 10, '/uploads/keycap-nahida.jpg', b'1', '2023-07-25 10:18:28.000000'),
(11, '2023-07-25 10:19:44.000000', 11, '/uploads/keycap-raiden.jpg', b'1', '2023-07-25 10:19:44.000000'),
(12, '2023-07-28 17:24:47.000000', 12, '/uploads/c91e85d4bb449309bc3afa2d6d399c57.jpg', b'1', '2023-07-28 17:24:47.000000'),
(13, '2023-07-28 17:24:47.000000', 12, '/uploads/cach-choi-raiden-genshin-impact-thong-tin-guide-skill-moi-thumb-640x360.jpg', b'1', '2023-07-28 17:24:47.000000'),
(14, '2023-08-01 13:42:26.000000', 13, '/uploads/4_99143bb64c414c71b6f04a7176c0266a.webp', b'1', '2023-08-01 13:42:26.000000'),
(15, '2023-08-01 13:42:26.000000', 13, '/uploads/1234_fc7d330045df46d2be1a3ed00cf0091b.webp', b'1', '2023-08-01 13:42:26.000000'),
(16, '2023-08-01 13:42:26.000000', 13, '/uploads/sg-11134201-22110-3gnrvfm5y0jv4a_tn.jpg', b'1', '2023-08-01 13:42:26.000000'),
(17, '2023-08-01 13:43:55.000000', 14, '/uploads/0a61af9b53ece6521063fa22e1123dda.jpg', b'1', '2023-08-01 13:43:55.000000'),
(18, '2023-08-01 13:43:55.000000', 14, '/uploads/1f0f067e-f128-485b-8667-67c99e922065.jpg', b'1', '2023-08-01 13:43:55.000000'),
(19, '2023-08-01 13:43:55.000000', 14, '/uploads/s-l1600.jpg', b'1', '2023-08-01 13:43:55.000000'),
(20, '2023-08-01 13:45:49.000000', 15, '/uploads/29-543ea244-d95a-4e32-938e-5a0ea43920d3.webp', b'1', '2023-08-01 13:45:49.000000'),
(21, '2023-08-01 13:45:49.000000', 15, '/uploads/61L-JuYpUIL.jpg', b'1', '2023-08-01 13:45:49.000000'),
(22, '2023-08-01 13:45:49.000000', 15, '/uploads/Kamado-Nezuko-SPM-Figure-1.jpeg', b'1', '2023-08-01 13:45:49.000000'),
(23, '2023-08-01 13:47:02.000000', 16, '/uploads/fig-Ayaka-4-510x510.jpg', b'1', '2023-08-01 13:47:02.000000'),
(24, '2023-08-01 13:47:02.000000', 16, '/uploads/kamisato-ayaka-fanmade-figure-merch-genshin.webp', b'1', '2023-08-01 13:47:02.000000'),
(25, '2023-08-01 13:47:02.000000', 16, '/uploads/kamisato-ayaka-fanmade-figure-merch-genshin-2.webp', b'1', '2023-08-01 13:47:02.000000'),
(26, '2023-08-01 13:48:17.000000', 17, '/uploads/299538867_1231430740991293_7938304530460684819_n_38866f09d053446581cb69936e0ee547.webp', b'1', '2023-08-01 13:48:17.000000'),
(27, '2023-08-01 13:48:17.000000', 17, '/uploads/aff05019f8880d462d6998fd0997d50f.jpg', b'1', '2023-08-01 13:48:17.000000'),
(28, '2023-08-01 13:48:17.000000', 17, '/uploads/s-l1200.jpg', b'1', '2023-08-01 13:48:17.000000'),
(29, '2023-08-01 13:50:05.000000', 18, '/uploads/61RUX6FAPBL._AC_UF894,1000_QL80_.jpg', b'1', '2023-08-01 13:50:05.000000'),
(30, '2023-08-01 13:50:05.000000', 18, '/uploads/s-l1600 (1).jpg', b'1', '2023-08-01 13:50:05.000000'),
(31, '2023-08-01 13:50:05.000000', 18, '/uploads/s-l1600 (2).jpg', b'1', '2023-08-01 13:50:05.000000'),
(32, '2023-08-01 13:51:19.000000', 19, '/uploads/61bon6bGYgL._AC_UF1000,1000_QL80_.jpg', b'1', '2023-08-01 13:51:19.000000'),
(33, '2023-08-01 13:51:19.000000', 19, '/uploads/a1f7eeb1ff21ab50ac813f74c39e23b3.jpg_720x720q80.jpg', b'1', '2023-08-01 13:51:19.000000'),
(34, '2023-08-01 13:51:19.000000', 19, '/uploads/s-l1200 (1).jpg', b'1', '2023-08-01 13:51:19.000000');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `user`
--

CREATE TABLE `user` (
  `id` bigint(20) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `role` enum('ADMIN','CUSTOMER') DEFAULT NULL,
  `status` bit(1) NOT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `user`
--

INSERT INTO `user` (`id`, `address`, `created_date`, `email`, `password`, `phone`, `role`, `status`, `updated_date`, `username`) VALUES
(1, 'address', '2023-07-19 09:16:08.000000', 'cuocsongmoihtt@gmail.com', '$2a$10$geSmx9.gxvAnkPX2wykjPu6mGpV1XBMS7sHBDWkSKjUsRlNal5q4a', '0795663386', 'CUSTOMER', b'1', '2023-07-19 09:16:08.000000', 'customer'),
(2, 'address', '2023-07-19 09:16:16.000000', 'cuocsongmoihtt@gmail.com', '$2a$10$bRHMfb9V5Ygol8AEgf3I7OyOaDpHNuoq1K2w67g10AfJxgQgxyuCC', '0795663386', 'ADMIN', b'1', '2023-07-19 09:16:16.000000', 'admin');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `user_payment`
--

CREATE TABLE `user_payment` (
  `id` bigint(20) NOT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `number_cart` varchar(255) DEFAULT NULL,
  `provider` varchar(255) DEFAULT NULL,
  `status` bit(1) NOT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `user_payment`
--

INSERT INTO `user_payment` (`id`, `created_date`, `number_cart`, `provider`, `status`, `updated_date`, `user_id`) VALUES
(1, '2023-07-19 10:45:40.000000', '1', 'Visa', b'1', '2023-07-19 10:45:40.000000', 1),
(2, '2023-07-19 11:11:44.000000', '1', 'Visa', b'1', '2023-07-19 11:11:44.000000', 2);

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `cart`
--
ALTER TABLE `cart`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `cart_item`
--
ALTER TABLE `cart_item`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `coupon`
--
ALTER TABLE `coupon`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `order_item`
--
ALTER TABLE `order_item`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `product_image`
--
ALTER TABLE `product_image`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `user_payment`
--
ALTER TABLE `user_payment`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `cart`
--
ALTER TABLE `cart`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT cho bảng `cart_item`
--
ALTER TABLE `cart_item`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT cho bảng `category`
--
ALTER TABLE `category`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT cho bảng `coupon`
--
ALTER TABLE `coupon`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT cho bảng `orders`
--
ALTER TABLE `orders`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT cho bảng `order_item`
--
ALTER TABLE `order_item`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT cho bảng `product`
--
ALTER TABLE `product`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT cho bảng `product_image`
--
ALTER TABLE `product_image`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=35;

--
-- AUTO_INCREMENT cho bảng `user`
--
ALTER TABLE `user`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT cho bảng `user_payment`
--
ALTER TABLE `user_payment`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
