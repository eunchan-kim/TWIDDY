#!/usr/bin/env python
# coding: utf-8

import pyjsonrpc

http_client = pyjsonrpc.HttpClient("http://localhost:10002", gzipped = True, debug = True)

# two kinds of method calling is possible.
# 1) http_client.call(method name, ...)
# 2) http_client.method_name(...)
print http_client.call("get_emotion", u"오늘은 너무나도 행복행복해요")
print http_client.get_emotion(u"헬로헬로")

# Notifications send messages to the server, without response.
#http_client.notify("add", 3, 4)