import os
import time

env = os.getenv('APP_ENV', 'production')
print(f"APP_ENV = {env}")
print("Container đang chạy...")

# Giữ container chạy
while True:
    time.sleep(60)
