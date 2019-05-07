-- phpMyAdmin SQL Dump
-- version 4.8.3
-- https://www.phpmyadmin.net/
--
-- Hôte : localhost
-- Généré le :  mar. 07 mai 2019 à 11:14
-- Version du serveur :  10.1.36-MariaDB
-- Version de PHP :  7.2.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données :  `parking`
--

-- --------------------------------------------------------

--
-- Structure de la table `parking`
--

CREATE TABLE `parking` (
  `id` int(11) NOT NULL,
  `pkgid` int(11) NOT NULL,
  `name` varchar(150) NOT NULL,
  `lat` double NOT NULL,
  `lng` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `parking_state`
--

CREATE TABLE `parking_state` (
  `id` int(11) NOT NULL,
  `pkgid` int(11) NOT NULL,
  `available` int(11) DEFAULT NULL,
  `last_update` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `rating`
--

CREATE TABLE `rating` (
  `id` int(11) NOT NULL,
  `pkgid` int(11) NOT NULL,
  `userid` varchar(32) NOT NULL,
  `state` int(11) NOT NULL,
  `date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `user_spots`
--

CREATE TABLE `user_spots` (
  `id` int(11) NOT NULL,
  `userid` varchar(32) NOT NULL,
  `spotid` varchar(32) NOT NULL,
  `name` varchar(150) NOT NULL,
  `lat` double NOT NULL,
  `lng` double NOT NULL,
  `date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `parking`
--
ALTER TABLE `parking`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `pkgid` (`pkgid`);

--
-- Index pour la table `parking_state`
--
ALTER TABLE `parking_state`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `pkgid` (`pkgid`,`last_update`);

--
-- Index pour la table `rating`
--
ALTER TABLE `rating`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `user_spots`
--
ALTER TABLE `user_spots`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `userid` (`userid`,`spotid`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `parking`
--
ALTER TABLE `parking`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `parking_state`
--
ALTER TABLE `parking_state`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `rating`
--
ALTER TABLE `rating`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `user_spots`
--
ALTER TABLE `user_spots`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
