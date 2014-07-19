#!/usr/bin/env python

import sys
import json
import operator
from collections import defaultdict

def hw(tweet_file):
    hashTagCount = defaultdict(int)
    with open(tweet_file) as f:
        for line in f:
            tweet = json.loads(line.strip())
            score = 0
            if tweet.has_key(u'entities'):
                hashtags = tweet[u'entities'][u'hashtags']
                for hashtag in hashtags:
                    if hashtag.has_key(u'text'):
                        hashTagCount[hashtag[u'text'].encode('utf-8')] += 1

    hashTagCount = sorted(hashTagCount.iteritems(), key=operator.itemgetter(1), reverse=True)
    for k, v in hashTagCount[:10]:
        print k, v


def lines(fp):
    print str(len(fp.readlines()))

def main():
    tweet_file = sys.argv[1]
    hw(tweet_file)

if __name__ == '__main__':
    main()
