#!/usr/bin/python3

import os
import re
import subprocess
from typing import Set


def run() -> None:
    env = os.environ.get('ENV', 'staging')
    new_commit_msgs = ''

    try:
        # get all the commit msgs since the previous tag for production env
        # to accommodate releases that include multiple PRs
        if env == 'production':
            previous_tag = subprocess.check_output(['git', 'describe', '--tags', '--abbrev=0', '@^'], stderr=subprocess.DEVNULL).decode('utf-8')
            new_commit_msgs = subprocess.check_output(['git', 'log', '--oneline', '%s..@' % previous_tag.rstrip()], stderr=subprocess.DEVNULL).decode('utf-8')
        # get only the most recent commit msg for non-production env
        else:
            new_commit_msgs = subprocess.check_output(['git', 'log', '-1', '--pretty=%B'], stderr=subprocess.DEVNULL).decode('utf-8')
    except BaseException as e:
        pass

    keys = find_issue_keys(new_commit_msgs)
    for k in keys:
        print(k)


def find_issue_keys(src: str) -> Set[str]:
    lines = src.split('\n')
    keys = set()
    patterns = [
        # [XXX-123] Some message
        re.compile('\[(\w+-\d+)].+$'),
        # Some message (XXX-123)
        re.compile('^.*\((\w+-\d+)\).*$'),
    ]
    for line in lines:
        line = line.strip()
        for p in patterns:
            s = p.search(line)
            if s:
                keys.add(s.group(1).upper())
                break
    return keys


if __name__ == '__main__':
    run()
