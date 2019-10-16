package org.jlab.clas12.fastMC.tests

import org.jlab.clas12.fastMC.swimmer.ParticleSwimmer
import org.jlab.jnp.geom.prim.Path3D
import org.jlab.jnp.physics.Particle


System.setProperty("JNP_DATA","/home/tylerviducic/research/clas12MagField");


Particle charged = Particle.createWithMassCharge(1, 1, 0.1, 0.1, 0.1, 0, 0, 0);
Particle uncharged = Particle.createWithMassCharge(1, 0, 0.1, 0.1, 0.1, 0, 0, 0);
Particle negcharged = Particle.createWithMassCharge(1, -1, 0.1, 0.1, 0.1, 0, 0, 0);

println(negcharged.charge());

ParticleSwimmer particleSwimmer = new ParticleSwimmer();

Path3D chargedPath = particleSwimmer.getParticlePath(charged);
Path3D unchargedPath = particleSwimmer.getParticlePath(uncharged);
Path3D negchargedPath = particleSwimmer.getParticlePath(negcharged);

chargedPath.show();
//unchargedPath.show();
negchargedPath.show();

