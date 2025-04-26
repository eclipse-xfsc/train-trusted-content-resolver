#!/usr/bin/env python3
# TODO to convert this script in GO.
from pathlib import Path
import csv
import http.client  # important, do not replace with request, keep script portable
import re

CURRENT_DIR = Path(__file__).parent
GO_SUM_FILE = CURRENT_DIR / '../trusted_content_resolver/go.sum'
THIRD_PARTY_OUTPUT_FILE = CURRENT_DIR / '../THIRD-PARTY.txt'


def detect_licenses() -> None:
    unique = set()
    lines = GO_SUM_FILE.read_text().splitlines()

    with THIRD_PARTY_OUTPUT_FILE.open('w', newline='') as write_file:
        writer = csv.writer(write_file)

        # headers
        writer.writerow(['# Licenses generated through https://pkg.go.dev/ API'])
        writer.writerow(["module name", "license"])

        conn = http.client.HTTPSConnection('pkg.go.dev')

        # "module name","license"
        for line in lines:
            split = line.split()
            url = split[0]

            if url not in unique:
                unique.add(url)
            else:
                continue

            print(f'requesting https://pkg.go.dev/{url}?tab=licenses...')

            conn.request("GET", f'/{url}?tab=licenses')
            response = conn.getresponse()
            content = response.read().decode()
            match = re.search('<div id="#lic-0">(.+?)</div>', content)
            license_name = match.group(1) if match else 'unknown'
            writer.writerow([url, license_name])


if __name__ == '__main__':
    detect_licenses()
