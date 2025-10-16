#!/usr/bin/env bash
set -euo pipefail
if ! rpm -qa | grep -qi '^redis'; then
  dnf install -y redis
fi
CONF="/etc/redis/redis.conf"
[ -f "$CONF" ] || CONF="/etc/redis.conf"
systemctl daemon-reload || true
systemctl enable redis || true
systemctl restart redis || systemctl start redis
systemctl status redis --no-pager -l || true
ss -lntp | grep ':6379' || true
