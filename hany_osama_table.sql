-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 01, 2025 at 01:06 PM
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
-- Database: `hany_osama_table`
--

-- --------------------------------------------------------

--
-- Table structure for table `courses`
--

CREATE TABLE `courses` (
  `db_id` int(11) NOT NULL,
  `courseCode` varchar(10) NOT NULL,
  `courseName` varchar(100) NOT NULL,
  `instructor` varchar(100) DEFAULT NULL,
  `credit_hours` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `courses`
--

INSERT INTO `courses` (`db_id`, `courseCode`, `courseName`, `instructor`, `credit_hours`) VALUES
(1, 'CS101', 'Intro to Computer Science', 'Dr. Emma Brown', 3),
(2, 'MATH201', 'Calculus I', 'Prof. John White', 4),
(3, 'PHY110', 'General Physics', 'Dr. Olivia Green', 3),
(4, 'CS102', 'Data Structures', 'Dr. Emma Brown', 3);

-- --------------------------------------------------------

--
-- Table structure for table `departments`
--

CREATE TABLE `departments` (
  `db_id` int(11) NOT NULL,
  `departmentName` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `departments`
--

INSERT INTO `departments` (`db_id`, `departmentName`) VALUES
(1, 'Computer Science'),
(2, 'Mathematics'),
(3, 'Physics');

-- --------------------------------------------------------

--
-- Table structure for table `department_courses`
--

CREATE TABLE `department_courses` (
  `department_id` int(11) NOT NULL,
  `courseCode` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `department_courses`
--

INSERT INTO `department_courses` (`department_id`, `courseCode`) VALUES
(1, 'CS101'),
(1, 'CS102'),
(3, 'PHY110');

-- --------------------------------------------------------

--
-- Table structure for table `instructors`
--

CREATE TABLE `instructors` (
  `db_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `ID` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `instructors`
--

INSERT INTO `instructors` (`db_id`, `name`, `ID`) VALUES
(1, 'Dr. Emma Brown', 'I001'),
(2, 'Prof. John White', 'I002'),
(3, 'Dr. Olivia Green', 'I003');

-- --------------------------------------------------------

--
-- Table structure for table `lab_courses`
--

CREATE TABLE `lab_courses` (
  `courseCode` varchar(10) NOT NULL,
  `labEquipment` varchar(255) DEFAULT NULL,
  `labHours` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `lab_courses`
--

INSERT INTO `lab_courses` (`courseCode`, `labEquipment`, `labHours`) VALUES
('CS102', 'PC Lab with IDEs and Debuggers', 3),
('PHY110', 'Oscilloscopes, Multimeters', 2);

-- --------------------------------------------------------

--
-- Table structure for table `registrars`
--

CREATE TABLE `registrars` (
  `db_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `ID` varchar(20) NOT NULL,
  `password` varchar(100) NOT NULL DEFAULT 'admin123'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `registrars`
--

INSERT INTO `registrars` (`db_id`, `name`, `ID`, `password`) VALUES
(1, 'Registrar Jane Doe', 'R001', 'admin123'),
(2, 'Registrar Mark Stone', 'R002', 'admin123'),
(3, 'Registrar Lisa Bloom', 'R003', 'lisa789'),
(4, 'Registrar Tom Hall', 'R004', 'tompass');

-- --------------------------------------------------------

--
-- Table structure for table `schedules`
--

CREATE TABLE `schedules` (
  `db_id` int(11) NOT NULL,
  `student_ID` varchar(20) DEFAULT NULL,
  `courseCode` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `schedules`
--

INSERT INTO `schedules` (`db_id`, `student_ID`, `courseCode`) VALUES
(1, 'S001', 'CS101'),
(2, 'S001', 'MATH201'),
(3, 'S002', 'PHY110'),
(4, 'S003', 'CS101'),
(5, 'S003', 'CS102'),
(7, '123456', 'MATH201'),
(8, '123456', 'CS101'),
(9, '123456', 'CS102');

-- --------------------------------------------------------

--
-- Table structure for table `students`
--

CREATE TABLE `students` (
  `db_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `ID` varchar(20) NOT NULL,
  `password` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `students`
--

INSERT INTO `students` (`db_id`, `name`, `ID`, `password`) VALUES
(1, 'Hany_Osama', '123456', '123456'),
(2, 'Alice Johnson', 'S001', 'pass123'),
(3, 'Bob Smith', 'S002', 'bobpass'),
(4, 'Charlie Lee', 'S003', 'charlie123'),
(5, 'Alice Johnson', 'S1001', ''),
(6, 'Bob Smith', 'S1002', ''),
(7, 'Carol Taylor', 'S1003', ''),
(8, 'David Brown', 'S1004', ''),
(9, 'Eva White', 'S1005', '');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `courses`
--
ALTER TABLE `courses`
  ADD PRIMARY KEY (`db_id`),
  ADD UNIQUE KEY `courseCode` (`courseCode`);

--
-- Indexes for table `departments`
--
ALTER TABLE `departments`
  ADD PRIMARY KEY (`db_id`);

--
-- Indexes for table `department_courses`
--
ALTER TABLE `department_courses`
  ADD PRIMARY KEY (`department_id`,`courseCode`),
  ADD KEY `courseCode` (`courseCode`);

--
-- Indexes for table `instructors`
--
ALTER TABLE `instructors`
  ADD PRIMARY KEY (`db_id`),
  ADD UNIQUE KEY `ID` (`ID`);

--
-- Indexes for table `lab_courses`
--
ALTER TABLE `lab_courses`
  ADD PRIMARY KEY (`courseCode`);

--
-- Indexes for table `registrars`
--
ALTER TABLE `registrars`
  ADD PRIMARY KEY (`db_id`),
  ADD UNIQUE KEY `ID` (`ID`);

--
-- Indexes for table `schedules`
--
ALTER TABLE `schedules`
  ADD PRIMARY KEY (`db_id`),
  ADD KEY `student_ID` (`student_ID`),
  ADD KEY `courseCode` (`courseCode`);

--
-- Indexes for table `students`
--
ALTER TABLE `students`
  ADD PRIMARY KEY (`db_id`),
  ADD UNIQUE KEY `ID` (`ID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `courses`
--
ALTER TABLE `courses`
  MODIFY `db_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `departments`
--
ALTER TABLE `departments`
  MODIFY `db_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `instructors`
--
ALTER TABLE `instructors`
  MODIFY `db_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `registrars`
--
ALTER TABLE `registrars`
  MODIFY `db_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `schedules`
--
ALTER TABLE `schedules`
  MODIFY `db_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `students`
--
ALTER TABLE `students`
  MODIFY `db_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `department_courses`
--
ALTER TABLE `department_courses`
  ADD CONSTRAINT `department_courses_ibfk_1` FOREIGN KEY (`department_id`) REFERENCES `departments` (`db_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `department_courses_ibfk_2` FOREIGN KEY (`courseCode`) REFERENCES `courses` (`courseCode`) ON DELETE CASCADE;

--
-- Constraints for table `lab_courses`
--
ALTER TABLE `lab_courses`
  ADD CONSTRAINT `lab_courses_ibfk_1` FOREIGN KEY (`courseCode`) REFERENCES `courses` (`courseCode`) ON DELETE CASCADE;

--
-- Constraints for table `schedules`
--
ALTER TABLE `schedules`
  ADD CONSTRAINT `schedules_ibfk_1` FOREIGN KEY (`student_ID`) REFERENCES `students` (`ID`) ON DELETE CASCADE,
  ADD CONSTRAINT `schedules_ibfk_2` FOREIGN KEY (`courseCode`) REFERENCES `courses` (`courseCode`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
