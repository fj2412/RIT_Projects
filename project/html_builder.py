"""
Author: Feng Jiang
CS 141 HTML Builder Project
Main Program
"""

import make_style
import make_content
import sys
from structs import *


###########
# for html#
###########
# image source string
IMAGE_SRC = '<img src="'
# class centering for image string
CLASS_CENTER = '" class="center">\n'
# start of head 2 string
H2 = "<h2>"
# end of head 2 string
END_H2 = "\n</h2>\n"
# start of paragraph string
P = "<p>"
# end of paragraph string
END_P = "\n</p>\n"


def prompt_body():
    """
    prompt the body for making the wizard mode from user input. It will ask multiple paragraphs if user intends to do so.
    A paragraph can contain multiple image files. So, user can input as many image as they want to a single paragraph.
    this function will return a list of different instances of class paragraph containing the informations for
    every particular paragraph.
    :return: a list of instances of paragraph class
    """
    h2 = []
    p_image = []
    end = "yes"
    while end == "yes" or end == "":
        p_title = input("Title of your paragraph:\t")
        print("Content of your paragraph (single line)")
        p_content = input()
        add_image = input("Do you want to add images? [yes]\t")
        while add_image == "yes" or add_image == "":
            image = input("Image file name:\t")
            p_image.append(image)
            add_image = input("Do you want to add another images? [yes]\t")
        paragraph = Paragraph(p_title, p_content, p_image)
        p_image = []
        h2.append(paragraph)
        end = input("Do you want to enter another paragraph to your website? [yes]\t")
    return h2


def create_head(css, title):
    """
    this function will create the head for the html given the css style template and the title of the html website.
    :param css: the pure css style template in strings
    :param title: the title of the webpage in strings
    :return: returns a string containing the complete head part of the html
    """
    doc = "<!DOCTYPE html>\n<html>\n"
    head = "<head>\n<title>" + title + "\n</title>" + css + "</head>\n"
    header = doc + head + "<body>\n<h1>" + title + "\n</h1>\n<hr/>\n"
    return header


def wizard_create_body(title):
    """
    using the body function and the title of the for the web page. The function will create the complete body portion of
    the html. it will concatenate strings until every paragraph in the list of the paragraph are concatenated into the
    full body. then it will return the pure body of html.
    :param title: the title of web page, a string
    :return: the body of html, a string
    """
    body = ""
    paragraphs = prompt_body()
    for paragraph in paragraphs:
        body += H2 + paragraph.title + END_H2
        body += P + paragraph.content + END_P
        for image in paragraph.images:
            body += IMAGE_SRC + image + CLASS_CENTER
    body += "</body>\n</html>"
    return body


def wizard_mode():
    """
    the wizard mode of the program. writes the html into a file named index.html by using the head and body strings
    created from the functions create_head() and wizard_create_body()
    """
    title = input("What is the title of your website?")
    css = make_style.create_css()
    html_head = create_head(css, title)
    html_body = wizard_create_body(title)
    html = html_head + html_body
    print("Your web page has been saved as index.html")
    file = open("index.html", "w+")
    file.write(html)
    file.close()


def website_create_body(website_info):
    """
    creates the body portion for the website mode given an instance of website_info dataclass as the argument.
    :param website_info:
    :return: a string of a body paragraph
    """
    body = ""
    body += H2 + website_info.title + END_H2
    body += '\n' + P + website_info.content + END_P
    for image in website_info.images:
        if isinstance(image, str):
            body += '<img src="' + image + CLASS_CENTER
        elif isinstance(image, Image):
            body += '<img src="' + image.name + '" width="' + image.size + CLASS_CENTER
        else:
            pass
    return body


def convert_txt_to_html(argv):
    """
    convert the text file into html format with the same name, to be used for writing the output
    :param argv: the argument from the terminal that user inputs
    :return: a name.html string with name being the same name as the text file and exist in directory
    """
    filename = argv.strip("txt")
    filename += "html"
    return filename


def web_site_mode():
    """
    the website mode of the program. it will create a css template and iterate through the argv values after argv[0].
    it will first create the links for every html file. Then it will build websites based on the different text files
    given from the parameter one by one by writing it into a html file with the same name as the text file in the same
    directory.
    """
    css = make_style.create_css()
    websites = dict()
    argument = dict()
    links = '<p align="center">'
    for args in range(1, len(sys.argv)):
        website, head = make_content.create_content(sys.argv[args])
        websites[head] = website
        argument[sys.argv[args]] = head
        filename = convert_txt_to_html(sys.argv[args])
        links += '<a href=' + filename + '>' + head + '</a>---'
    links += END_P
    for args in range(1, len(sys.argv)):
        html_body = ""
        header = create_head(css, argument[sys.argv[args]])
        html_head = header + links
        website = websites[argument[sys.argv[args]]]
        for website_info in website:
            html_body += website_create_body(website_info)
        filename = convert_txt_to_html(sys.argv[args])
        html = html_head + html_body
        file = open(filename, "+w")
        file.write(html)
        file.close()


def main():
    """
    the main function, test the system argument's length, if they are more than 1 they will be prompted with the wizard
    mode else they will be prompted with the web_site_mode.
    :return:
    """
    if len(sys.argv) == 1:
        wizard_mode()
    else:
        web_site_mode()


if __name__ == "__main__":
    main()
