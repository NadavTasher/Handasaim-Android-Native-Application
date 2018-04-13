package nadav.tasher.handasaim.values;

import java.util.Random;

public class Egg {
    public static final int TYPE_QUOTE = 0;
    public static final int TYPE_FACT = 1;
    public static final int TYPE_BOTH = 2;
    static String[] quotes = new String[]{
            "Love is like the wind, you can't see it but you can feel it.",
            "I'm not afraid of death; I just don't want to be there when it happens.",
            "All you need is love. But a little chocolate now and then doesn't hurt.",
            "When the power of love overcomes the love of power the world will know peace.",
            "For every minute you are angry you lose sixty seconds of happiness.",
            "Yesterday is history, tomorrow is a mystery, today is a gift of God, which is why we call it the present.",
            "The fool doth think he is wise, but the wise man knows himself to be a fool.",
            "In three words I can sum up everything I've learned about life: it goes on.",
            "You only live once, but if you do it right, once is enough.",
            "Two things are infinite: the universe and human stupidity; and I'm not sure about the universe.",
            "Life is pleasant. Death is peaceful. It's the transition that's troublesome.",
            "There are only two ways to live your life. One is as though nothing is a miracle. The other is as though everything is a miracle.",
            "We are not retreating - we are advancing in another Direction.",
            "The difference between fiction and reality? Fiction has to make sense.",
            "The right to swing my fist ends where the other man's nose begins.",
            "Denial ain't just a river in Egypt.",
            "Every day I get up and look through the Forbes list of the richest people in America. If I'm not there, I go to work.",
            "Advice is what we ask for when we already know the answer but wish we didn't",
            "The nice thing about egotists is that they don't talk about other people.",
            "Obstacles are those frightful things you see when you take your eyes off your goal.",
            "You can avoid reality, but you cannot avoid the consequences of avoiding reality.",
            "You may not be interested in war, but war is interested in you.",
            "Don't stay in bed, unless you can make money in bed.",
            "C makes it easy to shoot yourself in the foot; C++ makes it harder, but when you do, it blows away your whole leg.",
            "I have not failed. I've just found 10,000 ways that won't work.",
            "Black holes are where God divided by zero.",
            "The significant problems we face cannot be solved at the same level of thinking we were at when we created them.",
            "Knowledge speaks, but wisdom listens.",
            "Sleep is an excellent way of listening to an opera.",
            "Success usually comes to those who are too busy to be looking for it"
    };
    static String[] facts = new String[]{
            "In 1947, after the price of a chocolate bar increased from 5 cents to 8 cents, 200 kids marched and protested on the capitol building in British Columbia, shutting down the government for a day. It is known as \" The Candy Bar Strike\"",
            "The founders of Google were willing to sell Google for $1 million to Excite in 1999, but Excite turned them down. Google is now worth $527 Billion.",
            "During your lifetime, you will produce enough saliva to fill two swimming pools.",
            "Our eyes are always the same size from birth, but our nose and ears never stop growing.",
            "The chance of you dying on the way to get lottery tickets is actually greater than your chance of winning.",
            "The Guinness Book of World Records holds the record for being the book most often stolen from libraries.",
            "A sneeze travels at about 150 kilometers per hour.",
            "The average person spends six months of their lifetime waiting for a red light to turn green.",
            "Palm trees are part of the grass family.",
            "The average person falls asleep in seven minutes.",
            "The citrus soda 7-UP was created in 1929; '7' was selected after the original 7-ounce containers and 'UP' for the direction of the bubbles.",
            "Ten percent of the Russian government's income comes from the sale of vodka.",
            "Peanuts are one of the ingredients of dynamite.",
            "There are 293 ways to make change for a dollar.",
            "A shark is the only fish that can blink with both eyes.",
            "Two-thirds of the world’s eggplant is grown in New Jersey.",
            "A cat has 32 muscles in each ear.",
            "In most advertisements, including newspapers, the time displayed on a watch is 10:10.",
            "A dragonfly has a lifespan of 24 hours.",
            "A goldfish has a memory span of three seconds.",
            "The microwave was invented after a researcher walked by a radar tube and a chocolate bar melted in his pocket.",
            "The only domestic animal not mentioned in the Bible is the cat.",
            "Table tennis balls have been known to travel off the paddle at speeds up to 160 km/hr.",
            "Honey is the only natural food that is made without destroying any kind of life. What about milk you say? A cow has to eat grass to produce milk and grass are living.",
            "The lighter was invented ten years before the match was.",
            "It’s physically impossible for a pig to look up at the sky.",
            "A typical pencil can draw a line that is 63 kilometers long",
            "Astronauts get taller in space due to the lack of gravity."
    };

    public static String dispenseEgg(int type) {
        if (type == TYPE_QUOTE) {
            return "\"" + quotes[new Random().nextInt(quotes.length)] + "\"";
        } else if (type == TYPE_FACT) {
            return facts[new Random().nextInt(facts.length)];
        } else if (type == TYPE_BOTH) {
            if (new Random().nextBoolean()) {
                return dispenseEgg(TYPE_QUOTE);
            } else {
                return dispenseEgg(TYPE_FACT);
            }
        } else {
            return "No Egg For You Unfortunatly.";
        }
    }
}
