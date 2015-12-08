#!/usr/bin/env python
# coding: utf-8
'''
    Main developers: 김은찬
    Debuggers: 김은찬
'''
import requests
import json

def main():
    url = "http://143.248.142.86:4000/jsonrpc"
    headers = {'content-type': 'application/json'}

    payload = {
        "method": "get_emotion",
        "params": [u"행복행복행복"],
        "jsonrpc": "2.0",
        "id": 0,
    }
    response = requests.post(
        url, data=json.dumps(payload), headers=headers).json()
    print response
    print response["result"]

if __name__ == "__main__":
    main()
