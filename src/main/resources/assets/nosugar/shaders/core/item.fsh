#version 150

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float GameTime;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

const float DENSITY = 3000.0;
const float SIZE    = 0.05;
const float SPEED   = 1500.0;
const float RANGE   = 0.35;


float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123);
}

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    if (color.a < 0.1) discard;

    vec2 uv = texCoord0 * DENSITY;
    vec2 id = floor(uv);
    vec2 fuv = fract(uv);

    float h = hash(id);
    float time = GameTime * SPEED;

    vec2 center = vec2(0.5) + vec4(sin(time * h), cos(time * (1.1 - h)), 0.0, 0.0).xy * RANGE;

    vec2 diff = abs(fuv - center);
    float isSquare = step(diff.x, SIZE) * step(diff.y, SIZE);

    vec3 Color = vec3(1.0, 1.0, 1.0);
    vec3 finalRGB = mix(color.rgb, Color, isSquare);

    fragColor = vec4(finalRGB, color.a);
}