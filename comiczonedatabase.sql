-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 28, 2026 at 12:37 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `comiczonedatabase`
--

-- --------------------------------------------------------

--
-- Table structure for table `bookmarks`
--

CREATE TABLE `bookmarks` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `comic_id` int(11) DEFAULT NULL,
  `bookmarked_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `current_chapter` int(11) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `bookmarks`
--

INSERT INTO `bookmarks` (`id`, `user_id`, `comic_id`, `bookmarked_at`, `current_chapter`) VALUES
(2, 2, 1, '2026-06-23 06:56:11', 90),
(4, 3, 1, '2026-06-28 08:40:22', 110),
(5, 3, 12, '2026-06-28 10:19:29', 23),
(7, 3, 11, '2026-06-28 10:19:53', 1);

-- --------------------------------------------------------

--
-- Table structure for table `comics`
--

CREATE TABLE `comics` (
  `id` int(11) NOT NULL,
  `title` varchar(100) NOT NULL,
  `type` varchar(50) DEFAULT NULL,
  `genre` varchar(225) DEFAULT NULL,
  `image_path` varchar(255) DEFAULT NULL,
  `chapters` int(11) DEFAULT 0,
  `last_update` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `comics`
--

INSERT INTO `comics` (`id`, `title`, `type`, `genre`, `image_path`, `chapters`, `last_update`, `created_at`) VALUES
(1, 'I Killed an Academy Player', 'Manhwa', 'Action, Adventure, Fantasy', 'I_Killed_an_Academy_Player.png', 129, '2026-06-23 09:49:39', '2026-06-09 07:37:43'),
(7, 'Reincarnator’s Stream', 'Manhwa', 'Action, Adventure, Fantasy, Martial Arts, Reincarnation', 'Reincarnator’s_Stream.png', 56, '2026-06-28 09:50:57', '2026-06-28 09:50:57'),
(8, 'Solo Leveling', 'Manhwa', 'Action, Adventure, Fantasy, Shounen', 'Solo_Leveling.png', 200, '2026-06-28 09:53:05', '2026-06-28 09:53:05'),
(9, 'Villain To Kill', 'Manhwa', 'Action', 'Villain_To_Kill.png', 241, '2026-06-28 09:54:12', '2026-06-28 09:54:12'),
(10, 'The Extra’s Academy Survival Guide', 'Manhwa', 'Action, Adventure, Fantasy, Supernatural, Transmigrating', 'The_Extra’s_Academy_Survival_Guide.png', 112, '2026-06-28 09:56:11', '2026-06-28 09:56:11'),
(11, 'Blade of Sin', 'Manhua', 'Action, Fantasy', 'Blade_of_Sin.png', 0, '2026-06-28 09:57:36', '2026-06-28 09:57:36'),
(12, 'Martial Evolution Start by Awakening the King of Monsters', 'Manhua', 'Action, Fantasy', 'Martial_Evolution_Start_by_Awakening_the_King_of_Monsters.png', 135, '2026-06-28 09:59:13', '2026-06-28 09:59:13'),
(13, 'Infinite Evolution Starting from Zero', 'Manhua', 'Action, Fantasy', 'Infinite_Evolution_Starting_from_Zero.png', 103, '2026-06-28 10:00:59', '2026-06-28 10:00:59'),
(16, 'Black Clover', 'Manga', 'Action, Fantasy, Adventure', 'Black_Clover.png', 392, '2026-06-28 10:07:00', '2026-06-28 10:07:00'),
(17, 'Bleach', 'Manga', 'Action, Supernatural, Fantasy', 'Bleach.png', 686, '2026-06-28 10:08:49', '2026-06-28 10:08:49'),
(18, 'BORUTO - TWO BLUE VORTEX -', 'Manga', 'Action, Adventure, Martial Arts, Shounen', 'BORUTO_TWO_BLUE_VORTEX.png', 35, '2026-06-28 10:11:49', '2026-06-28 10:11:49'),
(19, 'One Piece', 'Manga', 'Action, Adventure, Fantasy', 'One_Piece.png', 1186, '2026-06-28 10:13:37', '2026-06-28 10:13:37'),
(20, 'SPY×FAMILY', 'Manga', 'Action, Comedy, Slice of Life', 'SPY×FAMILY.png', 130, '2026-06-28 10:15:29', '2026-06-28 10:15:29'),
(21, 'Lord of Summons! Sudden Mutation', 'Manhua', 'Action, Adventure, Fantasy', 'Lord_of_Summons!_Sudden_Mutation.png', 129, '2026-06-28 10:17:11', '2026-06-28 10:17:11'),
(22, 'Lord of Destiny Wheel', 'Manhua', 'Action, Adventure, Fantasy', 'Lord_of_Destiny_Wheel.png', 242, '2026-06-28 10:17:51', '2026-06-28 10:17:51');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('admin','user') DEFAULT 'user',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `role`, `created_at`) VALUES
(1, 'admin', 'admin123', 'admin', '2026-06-06 10:00:05'),
(2, 'rsy', 'rsy-4321', 'user', '2026-06-09 08:27:18'),
(3, 'resa', 'resacihuy', 'user', '2026-06-23 06:19:16');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `bookmarks`
--
ALTER TABLE `bookmarks`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `comic_id` (`comic_id`);

--
-- Indexes for table `comics`
--
ALTER TABLE `comics`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `bookmarks`
--
ALTER TABLE `bookmarks`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `comics`
--
ALTER TABLE `comics`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `bookmarks`
--
ALTER TABLE `bookmarks`
  ADD CONSTRAINT `bookmarks_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `bookmarks_ibfk_2` FOREIGN KEY (`comic_id`) REFERENCES `comics` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
