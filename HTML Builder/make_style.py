"""
Author: Feng Jiang
HTML Builder Project
CSS style maker using the style_template.txt file
"""

import turtle


# Style template file name
TEMPLATE_FILE = "style_template.txt"
# A set of font types
FONT_TYPE = {"Arial", "Comic Sans MS", "Lucida Grande", "Tahoma", "Verdana", "Helvetica", "Times New Roman"}
# font size of font type
FONT_SIZE = 14
# A set of variable colors
COLORS = {'peachpuff', 'slateblue', 'powderblue', 'lightcyan', 'chartreuse', 'moccasin',
'mediumseagreen', 'lawngreen', 'seagreen', 'mintcream', 'azure', 'goldenrod',
'lightblue', 'firebrick', 'lightseagreen', 'chocolate', 'yellowgreen', 'darkolivegreen',
'violet', 'ivory', 'sandybrown', 'wheat', 'mediumvioletred', 'bisque', 'lightgreen', 'cyan',
'hotpink', 'gray', 'indianred', 'antiquewhite', 'royalblue', 'yellow', 'indigo', 'lightcoral',
'darkslategrey', 'sienna', 'lightslategray', 'mediumblue', 'red', 'khaki', 'darkviolet',
'mediumorchid', 'darkblue', 'lightskyblue', 'turquoise', 'lightyellow', 'grey',
'whitesmoke', 'blueviolet', 'orchid', 'mediumslateblue', 'darkturquoise', 'coral',
'forestgreen', 'gainsboro', 'darkorange', 'cornflowerblue', 'lightsteelblue', 'plum',
'lavender', 'palegreen', 'darkred', 'dimgray', 'floralwhite', 'orangered', 'oldlace',
'darksalmon', 'lavenderblush', 'darkslategray', 'tan', 'cadetblue', 'silver', 'tomato',
'darkkhaki', 'slategray', 'maroon', 'olive', 'deeppink', 'linen', 'magenta', 'crimson',
'mistyrose', 'lime', 'saddlebrown', 'blanchedalmond', 'black', 'snow', 'seashell',
'darkcyan', 'gold', 'midnightblue', 'darkgoldenrod', 'palevioletred', 'fuchsia', 'teal',
'lightpink', 'darkgrey', 'mediumspringgreen', 'aquamarine', 'lightsalmon',
'navajowhite', 'darkgreen', 'burlywood', 'rosybrown', 'springgreen', 'purple',
'olivedrab', 'lightslategrey', 'orange', 'aliceblue', 'mediumaquamarine', 'navy', 'salmon',
'rebeccapurple', 'darkmagenta', 'limegreen', 'deepskyblue', 'pink', 'mediumpurple',
'skyblue', 'aqua', 'blue', 'slategrey', 'darkslateblue', 'honeydew', 'darkseagreen',
'paleturquoise', 'brown', 'thistle', 'lemonchiffon', 'peru', 'cornsilk', 'papayawhip',
'green', 'lightgoldenrodyellow', 'mediumturquoise', 'steelblue', 'lightgray', 'lightgrey',
'beige', 'palegoldenrod', 'darkgray', 'white', 'ghostwhite', 'dodgerblue', 'greenyellow',
'dimgrey', 'darkorchid'}


def prompt_style():
    """
    this function will prompt the user for the different colors and fonts for the css style template.
    :return: background_color, font, paragraph_color, head_color
    """
    font_numbers = {'0', '1', '2', '3', '4', '5', '6'}
    print("Background Color")
    background_color = str.lower(input("Choose the name of a color, or in format '#XXXXXX':\t"))
    if len(background_color) != 7 or background_color[0] != '#':
        while background_color not in COLORS:
            print("Illegal format")
            background_color = str.lower(input("Choose the color name or #XXXXXX\t"))
            if len(background_color) == 7 and background_color[0] == '#':
                break
    see_font = str.lower(input("Do you want to see what the fonts look like? [yes]\t"))
    if see_font == "yes" or see_font == "":
        print("Close the window when you have made your choice")
        turtle_fonts()
    print("Choose a font by its number",
          "0: Arial, size 14",
          "1: Comic Sans MS, size 14",
          "2: Lucida Grande, size 14",
          "3: Tahoma, size 14",
          "4: Verdana, size 14",
          "5: Helvetica, size 14",
          "6: Times New Roman, size 14", sep='\n')
    font = input(">> ")
    while font not in font_numbers:
        font = input("Invalid font number, enter from 0 - 6\t")
    if font == "0":
        font = "Arial"
    elif font == "1":
        font = "Comic Sans MS"
    elif font == "2":
        font = "Lucida Grande"
    elif font == "3":
        font = "Tahoma"
    elif font == "4":
        font = "Verdana"
    elif font == "5":
        font = "Helvetica"
    elif font == "6":
        font = "Times New Roman"
    print("Paragraph Text Color")
    paragraph_color = str.lower(input("Choose the name of a color, or in format '#XXXXXX':\t"))
    if len(paragraph_color) != 7 or paragraph_color[0] != '#':
        while paragraph_color not in COLORS:
            print("Illegal format")
            paragraph_color = str.lower(input("Choose the color name or #XXXXXX\t"))
            if len(paragraph_color) == 7 and paragraph_color[0] == '#':
                break
    print("Heading Color")
    head_color = str.lower(input("Choose the name of a color, or in format '#XXXXXX':\t"))
    if len(head_color) != 7 or head_color[0] != '#':
        while head_color not in COLORS:
            print("Illegal format")
            head_color = str.lower(input("Choose the color name or #XXXXXX\t"))
            if len(head_color) == 7 and head_color[0] == '#':
                break
    return background_color, font, paragraph_color, head_color


def turtle_fonts():
    """
    this function will allow the pop up of turtle window to show the user the different types of fonts
    :return:
    """
    turtle.hideturtle()
    turtle.title("Front Options")
    turtle.penup()
    turtle.setpos(-FONT_SIZE * 5, FONT_SIZE * 5)
    turtle.setup(FONT_SIZE * 20, FONT_SIZE * 20)
    turtle.right(90)
    turtle.write("Arial", align="left", font=("Arial", FONT_SIZE, "normal"))
    turtle.forward(FONT_SIZE * 2)
    turtle.write("Comic Sans MS", align="left", font=("Comic Sans MS", FONT_SIZE, "normal"))
    turtle.forward(FONT_SIZE * 2)
    turtle.write("Lucida Grande", align="left", font=("Lucida Grande", FONT_SIZE, "normal"))
    turtle.forward(FONT_SIZE * 2)
    turtle.write("Tahoma", align="left", font=("Tahoma", FONT_SIZE, "normal"))
    turtle.forward(FONT_SIZE * 2)
    turtle.write("Verdana", align="left", font=("Verdana", FONT_SIZE, "normal"))
    turtle.forward(FONT_SIZE * 2)
    turtle.write("Helvetica", align="left", font=("Helvetica", FONT_SIZE, "normal"))
    turtle.forward(FONT_SIZE * 2)
    turtle.write("Times New Roman", align="left", font=("Times New Roman", FONT_SIZE, "normal"))
    turtle.done()


def create_css():
    """
    this function create the css style template for the html file using the different configurations from the
    prompt style function
    :return: the pure css style template
    """
    background_color, font, paragraph_color, head_color = prompt_style()
    style = ""
    file = open(TEMPLATE_FILE)
    for line in file:
        search = True
        while search is True:
            if "@BACKCOLOR" in line:
                line = line.split("@BACKCOLOR")
                line = line[0] + background_color + line[1]
                search = True
            elif "@HEADCOLOR" in line:
                line = line.split("@HEADCOLOR")
                line = line[0] + head_color + line[1]
                search = True
            elif "@FONTSTYLE" in line:
                line = line.split("@FONTSTYLE")
                line = line[0] + font + line[1]
                search = True
            elif "@FONTCOLOR" in line:
                line = line.split("@FONTCOLOR")
                line = line[0] + paragraph_color + line[1]
                search = True
            else:
                style += line
                search = False
    style += '\n'
    file.close()
    return style
