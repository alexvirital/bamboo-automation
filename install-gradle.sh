
# 2014-09-12 taken from the internet by Alex Merenyi
# 
# installs to /opt/gradle
# existing versions are not overwritten/deleted
# seamless upgrades/downgrades
# $GRADLE_HOME points to latest *installed* (not released)
gradle_version=2.1
wget -N http://services.gradle.org/distributions/gradle-${gradle_version}-all.zip
sudo unzip -foq gradle-${gradle_version}-all.zip -d /opt/gradle
sudo ln -sfn gradle-${gradle_version} /opt/gradle/latest
sudo printf"export GRADLE_HOME=/opt/gradle/latest\nexport PATH=\$PATH:\$GRADLE_HOME/bin" > /etc/profile.d/gradle.sh
. /etc/profile.d/gradle.sh
# check installation
gradle -v