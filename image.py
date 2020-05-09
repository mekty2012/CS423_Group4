from PIL import Image
import random

def mask(px, w, h, p):
    for x in range(w):
        for y in range(h):
            if random.random() <= p:
                px[x, y] = (0, 0, 0)

def blur(px, w, h):
    dl = dict()
    for x in range(1, w-1):
        for y in range(1, h-1):
            r = px[x,y][0] / 4
            g = px[x,y][1] / 4
            b = px[x,y][2] / 4
            r += (px[x+1,y+1][0] + px[x+1,y-1][0] + px[x-1,y+1][0] + px[x-1,y-1][0]) / 8
            g += (px[x+1,y+1][1] + px[x+1,y-1][1] + px[x-1,y+1][1] + px[x-1,y-1][1]) / 8
            b += (px[x+1,y+1][2] + px[x+1,y-1][2] + px[x-1,y+1][2] + px[x-1,y-1][2]) / 8
            r += (px[x,y+1][0] + px[x,y-1][0] + px[x-1,y][0] + px[x+1,y][0]) / 16
            g += (px[x,y+1][1] + px[x,y-1][1] + px[x-1,y][1] + px[x+1,y][1]) / 16
            b += (px[x,y+1][2] + px[x,y-1][2] + px[x-1,y][2] + px[x+1,y][2]) / 16
            dl[(x,y)] = (int(r),int(g),int(b))
    for x in range(1,w-1):
        for y in range(1,h-1):
            px[x,y] = dl[(x,y)]

def blurx(px, w, h):
    dl = dict()
    for x in range(1, w-1):
        for y in range(h):
            r = px[x,y][0] / 2
            g = px[x,y][1] / 2
            b = px[x,y][2] / 2
            r += (px[x-1,y][0] + px[x+1,y][0]) / 4
            g += (px[x-1,y][1] + px[x+1,y][1]) / 4
            b += (px[x-1,y][2] + px[x+1,y][2]) / 4
            dl[(x,y)] = (int(r), int(g), int(b))
    for x in range(1, w-1):
        for y in range(h):
            px[x,y] = dl[(x,y)]

def blury(px, w, h):
    dl = dict()
    for x in range(w):
        for y in range(1, h-1):
            r = px[x,y][0] / 2
            g = px[x,y][1] / 2
            b = px[x,y][2] / 2
            r += (px[x,y-1][0] + px[x,y+1][0]) / 4
            g += (px[x,y-1][1] + px[x,y+1][1]) / 4
            b += (px[x,y-1][2] + px[x,y+1][2]) / 4
            dl[(x,y)] = (int(r), int(g), int(b))
    for x in range(w):
        for y in range(1, h-1):
            px[x,y] = dl[(x,y)]

def main():
    with Image.open('rika_original.jpg') as im:
        px = im.load()
        mask(px, 960, 640, 0.4)
        im.save('rika_mask.jpg', "JPEG")

    with Image.open('rika_original.jpg') as im:
        px = im.load()
        blur(px, 960, 640)
        blur(px, 960, 640)
        blur(px, 960, 640)
        blur(px, 960, 640)
        blur(px, 960, 640)
        im.save('rika_blur.jpg', "JPEG")

    with Image.open('rika_original.jpg') as im:
        px = im.load()
        blurx(px, 960, 640)
        blurx(px, 960, 640)
        blurx(px, 960, 640)
        blurx(px, 960, 640)
        blur(px, 960, 640)
        blur(px, 960, 640)
        blur(px, 960, 640)
        im.save('rika_blurx.jpg', "JPEG")

    with Image.open('rika_original.jpg') as im:
        px = im.load()
        blury(px, 960, 640)
        blury(px, 960, 640)
        blury(px, 960, 640)
        blury(px, 960, 640)
        blur(px, 960, 640)
        blur(px, 960, 640)
        blur(px, 960, 640)
        im.save('rika_blury.jpg', "JPEG")

if __name__ == "__main__":
    main()
