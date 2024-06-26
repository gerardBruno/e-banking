# e-banking
e banking to use Angular
Ce projet permet de CONNECTER  Angular et créer un pipeline CI/CD 

Configurer un pipeline CI/CD utilisant GitHub, Jenkins, SonarQube et Artifactory avec Maven implique plusieurs étapes. Voici un guide détaillé pour vous aider à mettre en place ce pipeline :

Pré-requis
GitHub : Un dépôt contenant votre projet Maven.
Jenkins : Un serveur Jenkins configuré.
SonarQube : Un serveur SonarQube configuré.
Artifactory : Un serveur Artifactory configuré.
Maven : Outil de gestion de projets et de build.
Étapes
1. Configurer le Dépôt GitHub
Créer un Dépôt GitHub : Si ce n'est pas déjà fait, créez un dépôt GitHub pour votre projet Maven.
Ajouter un Fichier Jenkinsfile : Ce fichier définira les étapes de votre pipeline Jenkins.
groovy
Copier le code
pipeline {
    agent any

    environment {
        // Variables d'environnement
        SONARQUBE_SERVER = 'SonarQube' // Nom du serveur SonarQube configuré dans Jenkins
        ARTIFACTORY_SERVER = 'Artifactory' // Nom du serveur Artifactory configuré dans Jenkins
        MAVEN_HOME = tool name: 'M3', type: 'maven' // Nom de l'installation de Maven configurée dans Jenkins
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/votre-utilisateur/nom-du-repo.git', branch: 'main'
            }
        }
        stage('Build') {
            steps {
                sh "${MAVEN_HOME}/bin/mvn clean install"
            }
        }
        stage('SonarQube Analysis') {
            environment {
                scannerHome = tool 'SonarQubeScanner'
            }
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh "${MAVEN_HOME}/bin/mvn sonar:sonar"
                }
            }
        }
        stage('Publish to Artifactory') {
            steps {
                rtMavenDeployer(
                    id: 'deployer-unique-id',
                    serverId: ARTIFACTORY_SERVER,
                    releaseRepo: 'libs-release-local',
                    snapshotRepo: 'libs-snapshot-local'
                )
                rtMavenRun(
                    tool: 'M3',
                    pom: 'pom.xml',
                    goals: 'deploy',
                    deployerId: 'deployer-unique-id'
                )
            }
        }
    }
    post {
        always {
            junit '**/target/surefire-reports/*.xml'
            cleanWs()
        }
    }
}
2. Configurer Jenkins
Installer les Plugins Nécessaires :

Git
Pipeline
SonarQube Scanner
Artifactory
Configurer SonarQube :

Accédez à Manage Jenkins > Configure System.
Ajoutez votre serveur SonarQube dans la section SonarQube servers.
Configurer Artifactory :

Accédez à Manage Jenkins > Configure System.
Ajoutez votre serveur Artifactory dans la section Artifactory.
Configurer Maven :

Accédez à Manage Jenkins > Global Tool Configuration.
Ajoutez une installation Maven.
Créer un Nouveau Job de Pipeline :

Nom du job : Pipeline
Type de job : Pipeline
Dans la section Pipeline du job, sélectionnez Pipeline script from SCM.
SCM : Git
URL du dépôt : https://github.com/votre-utilisateur/nom-du-repo.git
Branche : main
Script Path : Jenkinsfile
3. Configurer SonarQube
Créer un Projet dans SonarQube :

Accédez à SonarQube et créez un nouveau projet.
Notez le Project Key et le Token généré.
Configurer le Scanner SonarQube :

Ajoutez le plugin SonarQube Scanner à votre pom.xml :
xml
Copier le code
<properties>
    <sonar.host.url>http://your-sonarqube-server</sonar.host.url>
    <sonar.login>your-sonarqube-token</sonar.login>
</properties>
4. Configurer Artifactory
Créer des Repositories :

Créez des repositories pour les releases et les snapshots dans Artifactory.
Configurer les Déployements dans le pom.xml :

xml
Copier le code
<distributionManagement>
    <snapshotRepository>
        <id>snapshots</id>
        <url>http://your-artifactory-server/libs-snapshot-local</url>
    </snapshotRepository>
    <repository>
        <id>releases</id>
        <url>http://your-artifactory-server/libs-release-local</url>
    </repository>
</distributionManagement>
Exécution du Pipeline
Déclencher le Pipeline :

Accédez à Jenkins et déclenchez le job de pipeline.
Vérifier les Résultats :

Vérifiez les résultats de chaque étape dans Jenkins.
Consultez les rapports SonarQube pour l'analyse du code.
Vérifiez Artifactory pour les artefacts déployés.
En suivant ces étapes, vous devriez être en mesure de configurer un pipeline CI/CD complet utilisant GitHub, Jenkins, SonarQube et Artifactory avec Maven.


