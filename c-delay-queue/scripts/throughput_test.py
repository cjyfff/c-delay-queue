import requests
import uuid
import time

URL = 'http://127.0.0.1:8820/api-feign/dq/acceptMsg'


def main():

    for i in range(1000):
        data = {
            "taskId": uuid.uuid4().hex,
            "functionName": "exampleHandler",
            "delayTime": 20,
            "params": "{}",
            "retryCount": 0,
            "retryInterval": 1,
            "nonceStr": uuid.uuid4().hex
        }

        resp = requests.post(URL, json=data)
        print(resp.json())
	time.sleep(0.05)


if __name__ == '__main__':
    main()
