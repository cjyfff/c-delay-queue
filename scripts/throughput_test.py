import requests
import uuid

URL = 'http://10.7.247.119:9999/dq/acceptMsg'


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

        requests.post(URL, json=data)


if __name__ == '__main__':
    main()
