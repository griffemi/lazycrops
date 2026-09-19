from tkinter import Image

from PIL import Image

import yaml

import os

with open("scripts/config.yml", "r") as stream:
    try:
        config = yaml.safe_load(stream)
    except yaml.YAMLError as exc:
        print(exc)

def invert(path: str):
    image = Image.open(path).convert('RGBA')
    output = []
    for pix in image.getdata():
        if pix[3] in list(range(1, 256)):
            output.append((0, 0, 0, 0))
        else:
            output.append((0, 0, 0, 255))
    image.putdata(output)
    image.save(path)

for mat in os.listdir("scripts/assets/materials/"):
    if mat.endswith(".png"):
        path = "scripts/assets/materials/" + mat
        for stage in os.listdir("scripts/assets/crops/wheat/"):
            stage_path = "scripts/assets/crops/wheat/" + stage
            material = Image.open(path)
            material.save(path)
            stage_img = Image.open(stage_path).convert('RGBA')
            stage_path = "scripts/assets/temp/wheat/" + stage
            stage_img.save(stage_path)
            stage_img = Image.open(stage_path).convert('RGBA')
            mask = Image.open(stage_path).convert('LA')
            invert(stage_path)
            Image.composite(material.convert('RGB'), stage_img.convert("RGB"), mask).save(f"src/main/resources/assets/resourcecrops/textures/block/" + mat.split(".")[0] + "_crop_" + stage.split("_")[1].split(".")[0] + ".png")

# Basically, we just remove the pure white pixels from the textures and replace them with transparent pixels.
for texture in os.listdir("src/main/resources/assets/resourcecrops/textures/block/"):
    if texture.endswith(".png"):
        replaced = []
        for pix in Image.open("src/main/resources/assets/resourcecrops/textures/block/" + texture).convert("RGBA").getdata():
            if pix[0] == 0 and pix[1] == 0 and pix[2] == 0 and pix[3] == 255:
                replaced.append((0, 0, 0, 0))
            else:
                replaced.append(pix)
        finish = Image.open("src/main/resources/assets/resourcecrops/textures/block/" + texture).convert("RGBA")
        finish.putdata(replaced)
        finish.save("src/main/resources/assets/resourcecrops/textures/block/" + texture)

for mat in os.listdir("scripts/assets/materials/"):
    if mat.endswith(".png"):
        material_texture = Image.open("scripts/assets/materials/" + mat).convert("RGBA")
        seed_texture = Image.open("scripts/assets/seed.png").convert("RGBA")
        material_texture.save("scripts/assets/temp/materials/" + mat)
        seed_texture.save("scripts/assets/temp/seeds/" + mat)
        material_texture = Image.open("scripts/assets/temp/materials/" + mat).convert("RGBA")
        seed_texture = Image.open("scripts/assets/temp/seeds/" + mat).convert("RGBA")
        mask = Image.open("scripts/assets/temp/seeds/" + mat).convert("LA")
        invert("scripts/assets/temp/seeds/" + mat)
        Image.composite(material_texture.convert('RGB'), seed_texture.convert("RGB"), mask).save(f"src/main/resources/assets/resourcecrops/textures/item/" + mat.split(".")[0] + "_seeds.png")
        replaced = []
        for pix in Image.open("src/main/resources/assets/resourcecrops/textures/item/" + mat.split(".")[0] + "_seeds.png").convert("RGBA").getdata():
            if pix[0] == 0 and pix[1] == 0 and pix[2] == 0 and pix[3] == 255:
                replaced.append((0, 0, 0, 0))
            else:
                replaced.append(pix)
        finish = Image.open("src/main/resources/assets/resourcecrops/textures/item/" + mat.split(".")[0] + "_seeds.png").convert("RGBA")
        finish.putdata(replaced)
        finish.save("src/main/resources/assets/resourcecrops/textures/item/" + mat.split(".")[0] + "_seeds.png")