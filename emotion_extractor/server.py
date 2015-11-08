#!/usr/bin/env python
# coding: utf-8

import pyjsonrpc
import emotion_extractor

class RequestHandler(pyjsonrpc.HttpRequestHandler):
	@pyjsonrpc.rpcmethod
	def get_emotion(self, sentence):
		return emotion_extractor.get_emotion(sentence)
		
# Threading HTTP-serveer
http_server = pyjsonrpc.ThreadingHttpServer(
    server_address = ('localhost', 10002),
    RequestHandlerClass = RequestHandler
)

print "Starting HTTP server ..."
print "URL: http://localhost:10002"
http_server.serve_forever()
