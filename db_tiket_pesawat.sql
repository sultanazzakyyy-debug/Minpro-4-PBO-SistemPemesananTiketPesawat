-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Oct 13, 2026 at 06:46 AM
-- Server version: 8.0.30
-- PHP Version: 8.1.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_tiket_pesawat`
--

-- --------------------------------------------------------

--
-- Table structure for table `penerbangan`
--

CREATE TABLE `penerbangan` (
  `kode_penerbangan` varchar(10) NOT NULL,
  `asal` varchar(50) NOT NULL,
  `tujuan` varchar(50) NOT NULL,
  `jam_keberangkatan` varchar(5) NOT NULL,
  `harga` double NOT NULL,
  `kursi_tersedia` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `penerbangan`
--

INSERT INTO `penerbangan` (`kode_penerbangan`, `asal`, `tujuan`, `jam_keberangkatan`, `harga`, `kursi_tersedia`) VALUES
('GA401', 'Balikpapan', 'Jakarta', '08.00', 1500000, 9),
('JT510', 'Samarinda', 'Surabaya', '10.30', 1200000, 4);

-- --------------------------------------------------------

--
-- Table structure for table `penumpang`
--

CREATE TABLE `penumpang` (
  `id_penumpang` int NOT NULL,
  `nama` varchar(100) NOT NULL,
  `no_ktp` varchar(16) NOT NULL,
  `no_telepon` varchar(13) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `penumpang`
--

INSERT INTO `penumpang` (`id_penumpang`, `nama`, `no_ktp`, `no_telepon`) VALUES
(1, 'Sultan', '6471011503990001', '081234567890'),
(2, 'Azzaky', '6471022008000002', '082345678901');

-- --------------------------------------------------------

--
-- Table structure for table `tiket`
--

CREATE TABLE `tiket` (
  `id_tiket` int NOT NULL,
  `id_penumpang` int NOT NULL,
  `kode_penerbangan` varchar(10) NOT NULL,
  `kelas` varchar(10) NOT NULL,
  `status_tiket` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `tiket`
--

INSERT INTO `tiket` (`id_tiket`, `id_penumpang`, `kode_penerbangan`, `kelas`, `status_tiket`) VALUES
(1001, 1, 'GA401', 'EKONOMI', 'Dipesan'),
(1002, 2, 'JT510', 'BISNIS', 'Lunas');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `penerbangan`
--
ALTER TABLE `penerbangan`
  ADD PRIMARY KEY (`kode_penerbangan`);

--
-- Indexes for table `penumpang`
--
ALTER TABLE `penumpang`
  ADD PRIMARY KEY (`id_penumpang`);

--
-- Indexes for table `tiket`
--
ALTER TABLE `tiket`
  ADD PRIMARY KEY (`id_tiket`),
  ADD KEY `id_penumpang` (`id_penumpang`),
  ADD KEY `kode_penerbangan` (`kode_penerbangan`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `penumpang`
--
ALTER TABLE `penumpang`
  MODIFY `id_penumpang` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `tiket`
--
ALTER TABLE `tiket`
  MODIFY `id_tiket` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1003;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `tiket`
--
ALTER TABLE `tiket`
  ADD CONSTRAINT `tiket_ibfk_1` FOREIGN KEY (`id_penumpang`) REFERENCES `penumpang` (`id_penumpang`),
  ADD CONSTRAINT `tiket_ibfk_2` FOREIGN KEY (`kode_penerbangan`) REFERENCES `penerbangan` (`kode_penerbangan`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
