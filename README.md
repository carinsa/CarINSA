# CarINSA

## Manuel d'utilisation

### 1ère étape: cloner le projet et l'ouvrir avec android studio :

1-  Installer Android Studio
2-  New project from Version Control -> Git -> saisir le lien git du projet

### 2ème étape: mise en place du serveur :

1-  Installer le logiciel WAMP

2-  Copier tous les fichiers du dossier backend dans le répertoire suivant : ~/opt/lampp/htdocs/smart/ (dossier smart à créer)

3-  Aller sur l'interface phpMyAdmin via le lien suivant : http://localhost/phpmyadmin

4-  Créer la base de donnée : Databases -> ajouter une base de donnée sous le nom "parking" -> create

5-  Importer le shéma de la base : Import -> Browse.. -> Séléctionner le fichier parking.sql -> Go

La base de données est mise en place.

### 3ème étape: configurations :

1-  Dans le fichier "BackendAPI.java", modifier la valeur de la variable "IP" en la remplacant par votre ip public.

2-  Dans le fichier "GrandLyon.php", modifier la valeur de la variable "password" en la remplacant avec le mot de passe de l'api Grand Lyon que nous vous avons communiquer.

--Attention : en cas d'utilisation d'un téléphone portable pour tester, il faut exposer le port 80 pour permettre la connexion entre le telephone et l'ordinateur. Les deux appareils doivent bien sur être dans le même réseau.

### 4ème étape: Installer l'application et la tester

Il suffit de brancher un téléphone à l'ordinateur, activer le mode débogage, et de lancer l'application.
