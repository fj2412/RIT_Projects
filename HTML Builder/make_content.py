"""
Author: Feng Jiang
HTML Builder Project
Making the contents from the custom format text for creating a web page
"""

from structs import *
import sys

########################
# for content text file#
########################
# a keyword for new paragraph
KEY_PARAGRAPH = "!new_paragraph"
# a keyword for title
KEY_TITLE = "!title"
# a keyword for image
KEY_IMAGE = "!image"


def create_content(txt_file):
    """
    given a text file as the argument for the parameter, the function will create the list that contain the information
    of every single paragraph in the text file the user provides. it will look for KEYS that will indicate whether it
    is the start of a paragraph, if its a title, and if there are multiple images in the file. It will also test if
    the format in the text file is correct, if its incorrect it will exit out and print the error message.
    :param txt_file:
    :return:
    """
    file = open(txt_file)
    head = file.readline().strip()
    line = file.readline()
    html_info = []
    while line:
        content = ""
        images = []
        if KEY_PARAGRAPH in line:
            p_test = line.strip()
            if len(p_test) != len(KEY_PARAGRAPH):
                sys.exit("Extraneous information on certain " + KEY_PARAGRAPH + " line")
            line = file.readline()
            if KEY_TITLE not in line:
                sys.exit("Did not find certain " + KEY_TITLE + " in file, " + txt_file)
            line = line.strip(KEY_TITLE).strip()
            title = line
            line = file.readline()
            if KEY_PARAGRAPH in line or KEY_TITLE in line:
                sys.exit("Invalid format in file " + txt_file)
            while KEY_IMAGE not in line and KEY_PARAGRAPH not in line and line:
                content += line
                line = file.readline()
            while KEY_IMAGE in line:
                line = line.strip(KEY_IMAGE).strip().split()
                if len(line) == 1:
                    images += line
                elif len(line) == 2:
                    image = Image(line[0], line[1])
                    images.append(image)
                    if "%" not in image.size:
                        sys.exit("there is a missing % where required in file " + txt_file)
                else:
                    sys.exit("Invalid image entry in file " + txt_file)
                line = file.readline()
            info = WebsiteInfo(title, content, images)
            html_info.append(info)
            images = []
        else:
            line = file.readline()
    file.close()
    return html_info, head


