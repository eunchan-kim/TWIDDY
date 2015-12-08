#! /usr/bin/python2.7
# -*- coding: utf-8 -*-
'''
	Main developers: 김은찬
	Debuggers: 김은찬
'''
from werkzeug.wrappers import Request, Response
from werkzeug.serving import run_simple

from jsonrpc import JSONRPCResponseManager, dispatcher
import emotion_extractor

@dispatcher.add_method
def get_emotion(*args, **kwargs):
	print "get_emotion called"
	return emotion_extractor.get_emotion(args[0])

@Request.application
def application(request):
    # Dispatcher is dictionary {<method_name>: callable}
    dispatcher["echo"] = lambda s: s
    dispatcher["add"] = lambda a, b: a + b

    response = JSONRPCResponseManager.handle(
        request.data, dispatcher)
    return Response(response.json, mimetype='application/json')

if __name__ == '__main__':
    run_simple('143.248.142.86', 4000, application)
