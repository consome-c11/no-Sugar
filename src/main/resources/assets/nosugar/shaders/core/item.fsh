#version 150

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float GameTime;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

const float DENSITY = 4000.0;
const float SIZE    = 0.1;
const float SPEED   = 1200.0;
const float RANGE   = 0.4;

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123);
}

mat2 rotate2d(float a){
    return mat2(cos(a), -sin(a), sin(a), cos(a));
}

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    if (color.a < 0.1) discard;

    vec2 uv = texCoord0 * DENSITY;
    vec2 id = floor(uv);
    vec2 fuv = fract(uv);

    float h = hash(id);

    float individualSpeed = SPEED * (0.5 + h);
    float time = GameTime * individualSpeed + (h * 1000.0);

    vec2 center = vec2(0.5) + vec2(sin(time), cos(time * 0.8)) * RANGE;

    float angle = time * (h - 0.5) * 10.0;
    vec2 rotatedFuv = (fuv - center) * rotate2d(angle);

    vec2 diff = abs(rotatedFuv);
    float pSize = SIZE * (0.3 + h * 0.7);
    float isSquare = step(diff.x, pSize) * step(diff.y, pSize);

    float blink = smoothstep(0.7, 1.0, sin(time));

    vec3 sparkColor = vec3(1.0, 1.0, 1.0);

    vec3 finalRGB = mix(color.rgb, sparkColor, isSquare * blink);

    fragColor = vec4(finalRGB, color.a);
}