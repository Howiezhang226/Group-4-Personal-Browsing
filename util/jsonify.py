import re
import os
import json
import time
import dateparser
import operator

def jsonify(file):
    """

    :param file: a raw file
    :return: a json format string
    """
    f = open(file)
    mail = {}
    def construct_dict(s):
        """
        :param s: a stirng like To : a, b,c
        :return:
        """
        part = s.partition(":")
        mail[part[0]] = part[2].strip()
    start_read = False
    content = ""
    head = ""
    for line in f:
        if start_read:
            content += line
        else:
            head += line
        if line.strip() == "":
            start_read = True

    head = head.replace('\n', '').replace('\t', '').replace('\r', '')
    to = re.findall(r"(?<=To: )[\s\S]*(?=Subject:)", head)
    recepients = []
    if len(to) != 0:
        recepients = to[0].strip().replace(" ", "").split(",")
    id = re.findall(r"(?<=Message-ID: )[\s\S]*(?=Date:)", head)

    date = re.findall(r"(?<=Date: )[\s\S]*?(?=From:)", head)
    cc = re.findall(r"(?<=Cc:)[\s\S]*(?=Mime-Version:)", head)
    if len(cc) != 0:
        subject = re.findall(r"(?<=Subject: )[\s\S]*(?=Cc:)", head)
    else:
        subject = re.findall(r"(?<=Subject: )[\s\S]*(?=Mime-Version:)", head)
    bcc = re.findall(r"(?<=Bcc:)[\s\S]*(?=X-From:)", head)
    mail["To"] = recepients
    mail["Message-ID"] = id[0].strip()
    mail["Date"] = date[0]
    if len(subject) != 0:
        mail["Subject"] = subject[0]
    if len(cc) != 0:
        mail["To"] = mail["To"] + cc[0].replace(" ", "").strip().split(",")
    # if len(bcc) != 0:
    #     mail["To"] = mail["To"] + bcc[0].strip().split(",")
    mail["content"] = content.strip()
    count = len(re.findall(r'\w+', content))
    mail["cont_length"] = count
    f.close()
    return mail
if __name__ == '__main__':
    result = []
    for root, dirs, files in os.walk("sent_items2"):
        for afile in files:
            result.append(jsonify("sent_items2/" + afile))
    js = json.dumps(result, separators=(',',':'))
    fo = open("jeff.json", "wb")
    fo.write(js)
    fo.close()
    dic_date_proty = {}
    dic_2001_10 = {}
    for everyemail in result:
        time = everyemail["Date"]
        time = time[0:len(time) - 12]
        formated_time = dateparser.parse(time)
        if formated_time.month == 10 and formated_time.year == 2001:

            day = time.split(",")[0]
            timestamp = str(formated_time.month) + "/" + str(formated_time.day) + "/" + str(formated_time.year)
            for every_person in everyemail["To"]:
                if dic_2001_10.has_key(every_person):
                    dic_2001_10[every_person] += 1
                else:
                    dic_2001_10[every_person] = 1
            if dic_date_proty.has_key(timestamp):
                dic_date_proty[timestamp]["total"] += 1
                for every_person in everyemail["To"]:
                    if dic["sent_detail"].has_key(every_person):
                        dic["sent_detail"][every_person] += 1
                    else:
                        dic["sent_detail"][every_person] = 1
                dic_date_proty[timestamp]["recipient"] = dic_date_proty[timestamp]["recipient"].union(set(everyemail["To"]))
                dic_date_proty[timestamp]["daytime"][str(formated_time.hour/2)] += 1
            else:
                dic = {}
                dic["date"] = timestamp
                dic["sent_detail"] = {}
                for every_person in everyemail["To"]:
                    dic["sent_detail"][every_person] = 1
                dic["day"] = day
                dic["total"] = 1
                dic["recipient"] = set(everyemail["To"])
                for every_person in everyemail["To"]:
                    dic["sent_detail"][every_person] = 1
                dic["daytime"] = {
                    "0":0,
                    "1":0,
                    "2":0,
                    "3":0,
                    "4":0,
                    "5":0,
                    "6":0,
                    "7":0,
                    "8":0,
                    "9":0,
                    "10":0,
                    "11":0
                }
                dic["daytime"][str(formated_time.hour/2)] = 1
                dic_date_proty[timestamp] = dic
    list_dic_date_proty = []
    for i in dic_date_proty:
        dic_date_proty[i]["num_recipent"] = len(dic_date_proty[i]["sent_detail"])
        del dic_date_proty[i]["recipient"]
        list_dic_date_proty.append(dic_date_proty[i])
    sorted_dic_date_proty = sorted(dic_date_proty.items(), key=operator.itemgetter(0))
    js3 = json.dumps(list_dic_date_proty, separators=(',',':'))
    fi = open("jeff_2001_10.json", "wb")
    fi.write(js3)
    fi.close()
    sorted_dic_2001_10 = sorted(dic_2001_10.items(), key=operator.itemgetter(1), reverse=True)
    sorted_list = []
    for every_pair in sorted_dic_2001_10:
        sorted_list.append(every_pair[0])
    print sorted_dic_2001_10
    print sorted_list

    #print js3


