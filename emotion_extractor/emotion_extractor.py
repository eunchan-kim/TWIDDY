#! /usr/bin/python2.7
# -*- coding: utf-8 -*-
'''
	Used Open source library 'konlpy' from
	http://konlpy.org/ko/v0.4.3/#license
	For Korean NLP

	Main developers: 김은찬
	Debuggers: 김은찬
'''
from konlpy.tag import Kkma
from konlpy.utils import pprint
import urllib

kkma = Kkma()

positive = [u'기쁨', u'기쁘', u'흐뭇', u'평화', u'즐겁', u'즐거우', u'상쾌', u'행복', u'흡족', u'뿌듯', u'황홀', u'훈훈', 
			u'따뜻', u'유쾌', u'감격', u'감동', u'통쾌', u'후련', u'설레', u'신나', u'신남', u'산뜻', u'반갑', u'상큼',
			u'벅차', u'포근', u'짜릿', u'시원', u'선선', u'후련', u'신바람', u'아늑', u'끝내주', u'괜찮', u'화사', u'자유', 
			u'감미', u'감미롭', u'좋', u'자신감', u'고맙', u'감사', u'재밌', u'이쁘', u'사랑', u'공감', u'만족', u'보람차', u'보람', u'정겹', u'편안', u'홀가분']


sorrow_worry = [u'쓸쓸', u'외롭', u'외로', u'우울', u'슬프', u'불행', u'서럽', u'비참', u'불쌍', u'고독', u'허전', u'측은', 
				u'처참', u'암담', u'절망', u'침통', u'처량', u'비관', u'혼란', u'괴롭', u'걱정', u'근심', u'당황', u'불쾌', 
				u'울적', u'불편', u'심란', u'속상하', u'애잔', u'염려', u'답답', u'서글프', u'아프', u'아푸', u'야속', u'애석', 
				u'안타깝', u'부담', u'허무', u'허탈', u'참담', u'후회', u'북받치', u'막막', u'넌더리', u'무기력', u'힘들', u'피곤', u'엉엉', u'울', u'억울', u'상실감', u'상실', u'열등감', u'지겹']

anxiety = [u'두렵', u'무섭', u'불안', u'긴장', u'초조', u'주눅', u'떨리', u'무시', u'섬찟', u'부끄럽', u'창피', u'쪽팔리', u'죄책감']
anger = [u'증오', u'신경질', u'원망', u'경멸', u'분통', u'짜증', u'심술', u'분개', u'저주', u'모욕', u'얄밉', u'화난', u'밉', u'조롱', u'싫', u'혐오', u'역겹']

negative = sorrow_worry + anxiety + anger

test1 = u'아 오늘하루는 너무 피곤했다'
test2 = u'고기먹어서 행복행복'

test_sentence = [u'아 오늘하루는 너무 피곤했다', u'고기먹어서 행복행복', u'행복해요 덕분이에요 고마워요 선물이에요', u'꽃병 정말 병나게 좋아요..', 
				u'첨에 상담놀이 하자던 친구는 사라져버리고..ㅋㅋ잘~놀았뜨아~잘자요~나는 내일 해질때까지 자고싶다~~~~!!굿나잇',
				u'멤버들 한명한명 강한개성과 스타일로 열심히 촬영중입니다', u'사랑스러운 유라 모음', u'살아생전에도 효자는 아니었는데, 못난 아들때문에 이런 험한 소리까지 듣게 하니 자식된 마음이 무겁습니다.',
				u'지하철에서 눈이 너무 아파서 그냥 앞에 있는 사람들을 보는데 다 바닥에 발을 붙히고있는겨 옆을 봐도 신발이 땅에 척하고 다 붙어있길래 나도 땅에 발 붙여보려고 했는데 발이 살짝 떠서 슬퍼졌다 지금 굉장히 우울해졌다',
				u'서울지하철...무서워여...어떻게타이거..']


def convert_to_string(sentence):
    ret_string = u""
    for c in sentence.split():
        print c
        tmp = "0x"+c
        value = eval(tmp)
        print hex(value)
        ret_string += unichr(value)
    print ret_string
    return ret_string


def get_emotion(data):
        print "get emotion function"
        
        sentence = convert_to_string(data)
        pprint(sentence)
	nouns = kkma.nouns(sentence)
	pos = kkma.pos(sentence)

	pprint(nouns)
	pprint(pos)

	score = 0

	for noun in nouns:
		if noun in positive:
			pprint(noun)
			score += 1
		elif noun in negative:
			pprint(noun)
			score += -1

	for p in pos:
		word = p[0]
		if word in positive:
			pprint(word)
			score += 1
		elif word in negative:
			pprint(word)
			score -= 1

	print "score is " + str(score)

	if score > 0:
		print "positive sentence"
	elif score == 0:
		print "neutral sentence"
	else:
		print "negative sentence"

	return score

if __name__ == "__main__":
	# for test
	for s in test_sentence:
		get_emotion(s)




