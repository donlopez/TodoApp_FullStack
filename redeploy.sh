#!/usr/bin/env bash
set -euo pipefail

export CATALINA_HOME="/opt/homebrew/Cellar/tomcat@10/10.1.48/libexec"

echo "[1/6] Stopping Tomcat (ok if already stopped)…"
sh "$CATALINA_HOME/bin/shutdown.sh" || true
sleep 2

echo "[2/6] Cleaning old deployment…"
rm -rf "$CATALINA_HOME/webapps/TodoApp_FullStack" || true
rm -f  "$CATALINA_HOME/webapps/TodoApp_FullStack.war" || true

echo "[3/6] Building WAR…"
./mvnw -q -DskipTests package

echo "[4/6] Copying new WAR…"
cp target/TodoApp_FullStack-1.0-SNAPSHOT.war "$CATALINA_HOME/webapps/TodoApp_FullStack.war"

echo "[5/6] Starting Tomcat…"
sh "$CATALINA_HOME/bin/startup.sh"
sleep 2

echo "[6/6] Tailing logs (Ctrl+C to stop)…"
tail -n 200 -f "$CATALINA_HOME/logs/catalina.out"
