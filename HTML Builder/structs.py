"""
Author: Feng Jiang
CS 141 HTML Builder Project
Dataclass used for building building paragraphs.
"""

from dataclasses import dataclass


@dataclass(frozen=True)
class Paragraph:
    """
    A struct containing the information for a paragraph
    """
    title: str
    content: str
    images: list


@dataclass(frozen=True)
class Image:
    """
    A struct for information for a single image
    """
    name: str
    size: str


@dataclass(frozen=True)
class WebsiteInfo:
    """
    A struct containing the information of a single website with a list of images
    """
    title: str
    content: str
    images: list
