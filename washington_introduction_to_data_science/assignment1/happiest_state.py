#!/usr/bin/env python

import sys
import json
from collections import defaultdict

states = {
        'AK': 'Alaska',
        'AL': 'Alabama',
        'AR': 'Arkansas',
        'AS': 'American Samoa',
        'AZ': 'Arizona',
        'CA': 'California',
        'CO': 'Colorado',
        'CT': 'Connecticut',
        'DC': 'District of Columbia',
        'DE': 'Delaware',
        'FL': 'Florida',
        'GA': 'Georgia',
        'GU': 'Guam',
        'HI': 'Hawaii',
        'IA': 'Iowa',
        'ID': 'Idaho',
        'IL': 'Illinois',
        'IN': 'Indiana',
        'KS': 'Kansas',
        'KY': 'Kentucky',
        'LA': 'Louisiana',
        'MA': 'Massachusetts',
        'MD': 'Maryland',
        'ME': 'Maine',
        'MI': 'Michigan',
        'MN': 'Minnesota',
        'MO': 'Missouri',
        'MP': 'Northern Mariana Islands',
        'MS': 'Mississippi',
        'MT': 'Montana',
        'NA': 'National',
        'NC': 'North Carolina',
        'ND': 'North Dakota',
        'NE': 'Nebraska',
        'NH': 'New Hampshire',
        'NJ': 'New Jersey',
        'NM': 'New Mexico',
        'NV': 'Nevada',
        'NY': 'New York',
        'OH': 'Ohio',
        'OK': 'Oklahoma',
        'OR': 'Oregon',
        'PA': 'Pennsylvania',
        'PR': 'Puerto Rico',
        'RI': 'Rhode Island',
        'SC': 'South Carolina',
        'SD': 'South Dakota',
        'TN': 'Tennessee',
        'TX': 'Texas',
        'UT': 'Utah',
        'VA': 'Virginia',
        'VI': 'Virgin Islands',
        'VT': 'Vermont',
        'WA': 'Washington',
        'WI': 'Wisconsin',
        'WV': 'West Virginia',
        'WY': 'Wyoming'
}

def hw(sent_file, tweet_file):
    scores = {}     # initialize an empty dictionary
    with open(sent_file) as f:
        for line in f:
            term, score = line.split("\t")
            scores[term] = int(score)

    statesAbbrev = {}
    for k, v in states.iteritems():
        statesAbbrev[v] = k

    happiness = defaultdict(float)
    with open(tweet_file) as f:
        for line in f:
            tweet = json.loads(line.strip())
            score = 0
            if tweet.has_key(u'place') and tweet.has_key(u'text'):
                place = tweet[u'place']
                if place:
                    # Only consider US states
                    state = place[u'name'].encode('utf-8')
                    if state in statesAbbrev.keys():
                        score = 0.0
                        text = tweet['text'].encode('utf-8')
                        for word in text.split():
                            if word in scores.keys():
                                score = score + scores[word]
                        happiness[statesAbbrev[state]] += score

    happiestScore = float('-inf')
    happinessState = ''
    for k, v in happiness.iteritems():
        #print k, v
        if v > happiestScore:
            happinessState = k
            happiestScore = v
    print happinessState

def lines(fp):
    print str(len(fp.readlines()))

def main():
    sent_file = sys.argv[1]
    tweet_file = sys.argv[2]
    hw(sent_file, tweet_file)

if __name__ == '__main__':
    main()
